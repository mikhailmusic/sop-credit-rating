package rut.miit.grpc.sopcreditcalc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rut.miit.grpc.OfferRequest;
import rut.miit.grpc.OfferResponse;
import rut.miit.grpc.sopcreditcalc.model.OfferContext;
import rut.miit.grpc.sopcreditcalc.model.Offer;
import rut.miit.grpc.sopcreditcalc.model.ProductContext;
import rut.miit.grpc.sopcreditcalc.model.enums.RiskLevel;
import rut.miit.grpc.sopcreditcalc.service.OfferGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class OfferGeneratorImpl implements OfferGenerator {
    private static final Logger log = LoggerFactory.getLogger(OfferGeneratorImpl.class);
    private CreditCalcMapper mapper;

    private static final long  DEFAULT_TTL_DAYS = 7;
    private static final long  MIN_TTL_DAYS = 1;
    private static final long  MAX_TTL_DAYS = 20;

    @Override
    public OfferResponse generateOffer(OfferRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("OfferRequest cannot be null");
        }

        OfferContext context = mapper.toContext(request);

        if (context.getRiskLevel().equals(RiskLevel.HIGH)) {
            throw new IllegalStateException(String.format("Cannot generate offer for HIGH risk application: %s (score: %s)",
                    context.getApplicationId(), context.getCreditScore()));
        }

        ProductContext product = selectBestProduct(context);

        if (product == null) {
            throw new IllegalStateException(String.format("No suitable product found for application %s: purpose=%s, amount=%s, term=%d months",
                    context.getApplicationId(), context.getPurpose(), context.getAmount(), context.getTermMonths())
            );
        }

        BigDecimal apr = calculatePersonalizedApr(product, context.getCreditScore());
        BigDecimal monthlyPayment = calculateAnnuityMonthly(context.getAmount(), apr, context.getTermMonths());

        Duration ttl = calculateDynamicTtl(context, product);
        OffsetDateTime expiresAt = OffsetDateTime.now().plus(ttl);


        Offer offer = new Offer(
                context.getRequestId(), context.getClientId(), context.getApplicationId(), product.getProductId(),
                context.getAmount(), context.getTermMonths(), apr, monthlyPayment, expiresAt
        );

        log.info("Offer calculation completed: application={}, product={}, apr={}, ttl={} days",
                context.getApplicationId(), product.getProductId(), apr, ttl.toDays());

        return mapper.toResponse(offer);
    }


    private ProductContext selectBestProduct(OfferContext context) {
        List<ProductContext> candidates = context.getAvailableProducts();

        BigDecimal amount = context.getAmount();
        Integer termMonths = context.getTermMonths();

        ProductContext product = candidates.stream()
                .filter(p -> p.getPurpose().name().equalsIgnoreCase(context.getPurpose().name()))
                .filter(p -> amount.compareTo(p.getMinAmount()) >= 0 && amount.compareTo(p.getMaxAmount()) <= 0)
                .filter(p -> termMonths >= p.getMinTermMonths() && termMonths <= p.getMaxTermMonths())
                .min(Comparator.comparing(ProductContext::getBaseAprMin))
                .orElse(null);

        return product;
    }

    private BigDecimal calculatePersonalizedApr(ProductContext product, BigDecimal creditScore) {
        BigDecimal minRate = product.getBaseAprMin();
        BigDecimal maxRate = product.getBaseAprMax();
        BigDecimal rateSpan = maxRate.subtract(minRate);

        // score [0-100] -> [0-1]
        BigDecimal normalizedScore = creditScore
                .max(BigDecimal.ZERO)
                .min(toBDecimal(100))
                .divide(toBDecimal(100), 6, RoundingMode.HALF_UP);

        // Инверсия: высокий score = низкая ставка
        BigDecimal rateFactor = BigDecimal.ONE.subtract(normalizedScore);

        // APR = minRate + (maxRate - minRate) * (1 - normalizedScore)
        BigDecimal personalizedApr = minRate.add(rateSpan.multiply(rateFactor));

        return personalizedApr.setScale(2, RoundingMode.HALF_UP);
    }


    private BigDecimal calculateAnnuityMonthly(BigDecimal principal, BigDecimal aprPercent, int termMonths) {
        BigDecimal monthlyRate = aprPercent
                .divide(toBDecimal(100), 10, RoundingMode.HALF_UP)
                .divide(toBDecimal(12), 10, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(toBDecimal(termMonths), 2, RoundingMode.HALF_UP);
        }

        // (1 + r)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powerN = onePlusRate.pow(termMonths);

        // A = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(powerN);
        BigDecimal denominator = powerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }


    private Duration calculateDynamicTtl(OfferContext context, ProductContext product) {

        // На основе оценки (выше = дольше)
        double scoreMultiplier = calculateScoreMultiplier(context.getCreditScore());

        // Размер кредита (больше = короче)
        double amountMultiplier = calculateAmountMultiplier(context.getAmount());

        // Уровень риска
        double riskMultiplier = switch (context.getRiskLevel()) {
            case LOW -> 1.2;      // +20%
            case MEDIUM -> 1.0;   // стандарт
            case HIGH -> 0.5;     // -50%
            default -> 1.0;
        };

        // Тип продукта
        double productMultiplier = switch (product.getPurpose()) {
            case OVERDRAFT -> 0.3;
            case CONSUMER -> 0.7;
            case AUTO, MORTGAGE -> 1.5;
            default -> 1.0;
        };

        long finalDays = Math.round(DEFAULT_TTL_DAYS * scoreMultiplier * amountMultiplier * riskMultiplier * productMultiplier);

        finalDays = Math.max(MIN_TTL_DAYS, Math.min(MAX_TTL_DAYS, finalDays));

        log.debug("TTL calculated: score={}, amount={}, risk={} -> {} days", context.getCreditScore(),
                context.getAmount(), context.getRiskLevel(), finalDays);

        return Duration.ofDays(finalDays);
    }

    private double calculateScoreMultiplier(BigDecimal score) {
        if (score.compareTo(toBDecimal(90)) >= 0) return 1.5;
        if (score.compareTo(toBDecimal(80)) >= 0) return 1.3;
        if (score.compareTo(toBDecimal(70)) >= 0) return 1.1;
        if (score.compareTo(toBDecimal(60)) >= 0) return 1.0;
        return 0.7;
    }

    private double calculateAmountMultiplier(BigDecimal amount) {
        BigDecimal threshold1 = toBDecimal(1_000_000);
        BigDecimal threshold2 = toBDecimal(5_000_000);

        if (amount.compareTo(threshold2) >= 0) return 0.7;  // > 5M
        if (amount.compareTo(threshold1) >= 0) return 0.85; // 1-5M
        return 1.0;                                         // < 1M
    }


    private static BigDecimal toBDecimal(double v) { return BigDecimal.valueOf(v); }

    @Autowired
    public void setMapper(CreditCalcMapper mapper) {
        this.mapper = mapper;
    }
}
