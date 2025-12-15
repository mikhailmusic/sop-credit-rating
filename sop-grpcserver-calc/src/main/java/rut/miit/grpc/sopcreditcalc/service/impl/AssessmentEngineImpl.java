package rut.miit.grpc.sopcreditcalc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rut.miit.grpc.AssessmentRequest;
import rut.miit.grpc.AssessmentResponse;

import rut.miit.grpc.sopcreditcalc.model.*;
import rut.miit.grpc.sopcreditcalc.model.enums.EmploymentStatus;
import rut.miit.grpc.sopcreditcalc.model.enums.Purpose;
import rut.miit.grpc.sopcreditcalc.model.enums.RiskLevel;
import rut.miit.grpc.sopcreditcalc.service.AssessmentEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentEngineImpl implements AssessmentEngine {
    private static final Logger log = LoggerFactory.getLogger(AssessmentEngineImpl.class);
    private CreditCalcMapper mapper;

    private static final BigDecimal MAX_DSR_PERCENT = toBDecimal(70);
    private static final BigDecimal RISK_LOW = toBDecimal(80);
    private static final BigDecimal RISK_MED = toBDecimal(50);

    // Коэффициенты для агрегирования (0..100)
    private static final BigDecimal W_AGE = toBDecimal(1.00);
    private static final BigDecimal W_EMPL = toBDecimal(1.10);
    private static final BigDecimal W_PURPOSE = toBDecimal(0.90);
    private static final BigDecimal W_HISTORY = toBDecimal(1.00);
    private static final BigDecimal W_DSR = toBDecimal(1.30);
    private static final BigDecimal W_LTI = toBDecimal(1.10);
    private static final BigDecimal W_BEHAVIOR = toBDecimal(1.20);

    @Override
    public AssessmentResponse assess(AssessmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AssessmentRequest cannot be null");
        }
        AssessmentContext context = mapper.toContext(request);
        log.info("Starting assessment for application: {}, client: {}", context.getApplicationId(), context.getClientId());

        // Базовые метрики платёжеспособности
        FinancialRatios ratios = calculateFinancialRatios(context);

        // Жёсткие правила
        List<String> hardStops = applyHardStops(context, ratios);
        if (!hardStops.isEmpty()) {
            log.warn("Application {} rejected by hard stops: {}", context.getApplicationId(), hardStops);

            Assessment assessment = new Assessment(context.getRequestId(), context.getApplicationId(),
                    context.getClientId(), BigDecimal.ZERO, false, RiskLevel.HIGH, hardStops);
            return mapper.toResponse(assessment);
        }

        // Факторы риска
        RiskFactors riskFactors = calculateRiskFactors(context, ratios);

        // Агрегация (0..100)
        BigDecimal creditScore = aggregateScore(riskFactors);

        // Классификация риска + финальное решение
        RiskLevel riskLevel = classifyRisk(creditScore);
        boolean approved = switch (riskLevel) {
            case LOW, MEDIUM -> true;
            case HIGH -> false;
        };

        // Объяснения
        List<String> reasons = buildExplanations(riskFactors, riskLevel, approved);

        log.info("Assessment calculation completed: score={}, risk={}, approved={}", creditScore, riskLevel, approved);

        Assessment assessment = new Assessment(context.getRequestId(), context.getApplicationId(),
                context.getClientId(), creditScore, approved, riskLevel, reasons);
        return mapper.toResponse(assessment);
    }

    // Метрики платёжеспособности

    private FinancialRatios calculateFinancialRatios(AssessmentContext ctx) {
        BigDecimal monthlyIncome = ctx.getAnnualIncome().divide(toBDecimal(12), 6, RoundingMode.HALF_UP);

        BigDecimal dsrPercent = safeDiv(ctx.getTotalMonthlyDebtPayment(), monthlyIncome)
                .multiply(toBDecimal(100)); // DSR/DTI в %

        BigDecimal lti = safeDiv(ctx.getAmount(), ctx.getAnnualIncome()); // Loan-to-Income

        return new FinancialRatios(monthlyIncome, dsrPercent, lti);
    }

    private static BigDecimal safeDiv(BigDecimal a, BigDecimal b) {
        if (a == null || b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return a.divide(b, 6, RoundingMode.HALF_UP);
    }


    // Жёсткие правила

    private List<String> applyHardStops(AssessmentContext ctx, FinancialRatios ratios) {
        List<String> stops = new ArrayList<>();

        if (ctx.getAge() + (ctx.getTermMonths() / 12) > 75) {
            stops.add("Age at loan maturity exceeds maximum allowed");
        }
        if (EmploymentStatus.UNEMPLOYED.equals(ctx.getEmploymentStatus())) {
            stops.add("Unemployed applicants cannot be approved");
        }
        if (ctx.getMaxDaysOverdueLast12m() > 30) {
            stops.add("Severe payment delinquency in credit history");
        }
        if (ctx.getPaymentsDelayedLast12m() >= 3) {
            stops.add("Multiple payment failures in recent history");
        }

        if (ratios.getDsrPercent().compareTo(MAX_DSR_PERCENT) > 0) {
            stops.add("Total debt burden exceeds regulatory limit");
        }

        return stops;
    }


    // Факторы риска

    private RiskFactors calculateRiskFactors(AssessmentContext ctx, FinancialRatios ratios) {
        return new RiskFactors(
                ageFactor(ctx.getAge()),
                employmentFactor(ctx.getEmploymentStatus()),
                purposeFactor(ctx.getPurpose()),
                historyFactor(ctx.getHistoryApprovedCount(), ctx.getHistoryRejectedCount(), ctx.getPaymentsDelayedLast12m(), ctx.getMaxDaysOverdueLast12m()),
                dsrPenalty(ratios.getDsrPercent()),
                ltiPenalty(ratios.getLti()),
                behaviorFactor(ctx.getPaymentsDelayedLast12m(), ctx.getMaxDaysOverdueLast12m())
        );
    }

    private BigDecimal ageFactor(int age) {
        if (age < 21) return toBDecimal(-10);
        if (age <= 30) return toBDecimal(0);
        if (age <= 50) return toBDecimal(5);
        if (age <= 65) return toBDecimal(2);
        return toBDecimal(-5);
    }

    private BigDecimal employmentFactor(EmploymentStatus status) {
        return switch (status) {
            case EMPLOYED -> toBDecimal(6);
            case SELF_EMPLOYED -> toBDecimal(2);
            case UNEMPLOYED -> toBDecimal(-20);
            default -> toBDecimal(0);
        };
    }

    private BigDecimal purposeFactor(Purpose purpose) {
        return switch (purpose) {
            case MORTGAGE -> toBDecimal(7);
            case AUTO -> toBDecimal(4);
            case REFINANCE -> toBDecimal(5);
            case SECURED -> toBDecimal(5);
            case CONSUMER -> toBDecimal(0);
            case UNSECURED -> toBDecimal(-4);
            case BUSINESS -> toBDecimal(-3);
            case EDUCATION -> toBDecimal(2);
            case MEDICAL -> toBDecimal(3);
            case OVERDRAFT -> toBDecimal(-6);
            default -> toBDecimal(0);
        };
    }

    private BigDecimal historyFactor(int approved, int rejected, int delayed12m, int maxDpd12m) {
        BigDecimal factor = toBDecimal(0);

        if (approved >= 3) factor = factor.add(toBDecimal(5));
        else if (approved >= 1) factor = factor.add(toBDecimal(2));

        if (rejected > approved) factor = factor.subtract(toBDecimal(4));

        if (delayed12m > 0) factor = factor.subtract(toBDecimal(3).multiply(toBDecimal(delayed12m)));

        if (maxDpd12m > 15) factor = factor.subtract(toBDecimal(4));

        return factor;
    }

    private BigDecimal dsrPenalty(BigDecimal dsrPercent) {
        if (dsrPercent.compareTo(toBDecimal(20)) <= 0) return toBDecimal(6);
        if (dsrPercent.compareTo(toBDecimal(35)) <= 0) return toBDecimal(3);
        if (dsrPercent.compareTo(toBDecimal(50)) <= 0) return toBDecimal(-3);
        if (dsrPercent.compareTo(toBDecimal(60)) <= 0) return toBDecimal(-7);
        return toBDecimal(-10);
    }

    private BigDecimal ltiPenalty(BigDecimal lti) {
        if (lti.compareTo(toBDecimal(0.5)) <= 0) return toBDecimal(5);
        if (lti.compareTo(toBDecimal(1.0)) <= 0) return toBDecimal(2);
        if (lti.compareTo(toBDecimal(2.0)) <= 0) return toBDecimal(-3);
        return toBDecimal(-7);
    }

    private BigDecimal behaviorFactor(int delayed12m, int maxDpd12m) {
        if (delayed12m == 0 && maxDpd12m == 0) {
            return toBDecimal(5);
        }

        BigDecimal penalty = toBDecimal(0);
        if (delayed12m >= 1) penalty = penalty.subtract(toBDecimal(4));

        if (maxDpd12m > 15) penalty = penalty.subtract(toBDecimal(3));

        return penalty;
    }


    // Агрегация (0..100)

    private BigDecimal aggregateScore(RiskFactors factors) {
        // База 50 + сумма взвешенных факторов => clamp в [0..100]
        BigDecimal score = toBDecimal(50)
                .add(factors.getAgeFactor().multiply(W_AGE))
                .add(factors.getEmploymentFactor().multiply(W_EMPL))
                .add(factors.getPurposeFactor().multiply(W_PURPOSE))
                .add(factors.getHistoryFactor().multiply(W_HISTORY))
                .add(factors.getDsrPenalty().multiply(W_DSR))
                .add(factors.getLtiPenalty().multiply(W_LTI))
                .add(factors.getBehaviorFactor().multiply(W_BEHAVIOR));

        BigDecimal clamped = score.max(BigDecimal.ZERO).min(toBDecimal(100));

        return clamped.setScale(1, RoundingMode.HALF_UP);
    }

    private RiskLevel classifyRisk(BigDecimal score) {
        if (score.compareTo(RISK_LOW) >= 0) return RiskLevel.LOW;
        if (score.compareTo(RISK_MED) >= 0) return RiskLevel.MEDIUM;
        return RiskLevel.HIGH;
    }

    private List<String> buildExplanations(RiskFactors factors, RiskLevel riskLevel, boolean approved) {
        List<String> reasons = new ArrayList<>();

        if (!approved) {

            if (factors.getDsrPenalty().compareTo(BigDecimal.ZERO) < 0) reasons.add("High DSR/DTI");
            if (factors.getLtiPenalty().compareTo(BigDecimal.ZERO) < 0) reasons.add("High loan-to-income");
            if (factors.getHistoryFactor().compareTo(BigDecimal.ZERO) < 0)
                reasons.add("Negative credit history indicators");
            if (factors.getEmploymentFactor().compareTo(BigDecimal.ZERO) < 0) reasons.add("Employment status risk");
            if (factors.getPurposeFactor().compareTo(BigDecimal.ZERO) < 0) reasons.add("Risky loan purpose");
            if (factors.getBehaviorFactor().compareTo(BigDecimal.ZERO) < 0) reasons.add("Past payment behavior concerns");

        } else {
            reasons.add(switch (riskLevel) {
                case LOW -> "Excellent profile with low risk";
                case MEDIUM -> "Acceptable risk level";
                case HIGH -> "";
            });
        }

        return reasons;
    }

    private static BigDecimal toBDecimal(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }

    @Autowired
    public void setMapper(CreditCalcMapper mapper) {
        this.mapper = mapper;
    }
}
