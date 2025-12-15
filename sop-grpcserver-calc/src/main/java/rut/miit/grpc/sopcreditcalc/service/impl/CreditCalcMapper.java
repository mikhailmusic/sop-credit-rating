package rut.miit.grpc.sopcreditcalc.service.impl;

import org.springframework.stereotype.Component;
import rut.miit.grpc.*;
import rut.miit.grpc.sopcreditcalc.model.*;
import rut.miit.grpc.sopcreditcalc.model.enums.EmploymentStatus;
import rut.miit.grpc.sopcreditcalc.model.enums.Purpose;
import rut.miit.grpc.sopcreditcalc.model.enums.RiskLevel;

import java.math.BigDecimal;

@Component
public class CreditCalcMapper {

    AssessmentContext toContext(AssessmentRequest request) {
        return new AssessmentContext(
                request.getRequestId(), request.getApplicationId(), request.getClientId(), new BigDecimal(request.getAmount()),
                request.getTermMonths(), Purpose.valueOf(request.getPurpose().name()), new BigDecimal(request.getAnnualIncome()), new BigDecimal(request.getTotalMonthlyDebtPayment()),
                EmploymentStatus.valueOf(request.getEmploymentStatus().name()), request.getAge(),
                request.getHistoryApprovedCount(), request.getHistoryRejectedCount(), request.getPaymentsDelayedLast12M(),
                request.getMaxDaysOverdueLast12M(), request.getAvailableProductsList()
                .stream().map(snapshot -> toProductContext(snapshot)).toList()
        );
    }

    OfferContext toContext(OfferRequest request) {
        return new OfferContext(
                request.getRequestId(),
                request.getApplicationId(),
                request.getClientId(),
                new BigDecimal(request.getCreditScore()),
                RiskLevel.valueOf(request.getRiskLevel().name()),
                new BigDecimal(request.getAmount()),
                request.getTermMonths(),
                Purpose.valueOf(request.getPurpose().name()),
                request.getAvailableProductsList().stream().map(snapshot -> toProductContext(snapshot)).toList()
        );
    }

    ProductContext toProductContext(ProductSnapshot snapshot) {
        return new ProductContext(
                snapshot.getProductId(), Purpose.valueOf(snapshot.getPurpose().name()), new BigDecimal(snapshot.getMinAmount()),
                new BigDecimal(snapshot.getMaxAmount()), snapshot.getMinTermMonths(), snapshot.getMaxTermMonths(),
                new BigDecimal(snapshot.getBaseAprMin()), new BigDecimal(snapshot.getBaseAprMax())
        );
    }

    AssessmentResponse toResponse(Assessment result) {
        return AssessmentResponse.newBuilder()
                .setRequestId(result.getRequestId())
                .setApplicationId(result.getApplicationId())
                .setClientId(result.getClientId())
                .setCreditScore(result.getCreditScore().toPlainString())
                .setApproved(result.getApproved())
                .setRiskLevel(EnumRiskLevel.valueOf(result.getRiskLevel().name()))
                .addAllRejectionReasons(result.getRejectionReasons())
                .build();
    }

    OfferResponse toResponse(Offer result) {
        return OfferResponse.newBuilder()
                .setRequestId(result.getRequestId())
                .setClientId(result.getClientId())
                .setApplicationId(result.getApplicationId())
                .setProductId(result.getProductId())
                .setApprovedAmount(result.getApprovedAmount().toPlainString())
                .setTermMonths(result.getTermMonths())
                .setAnnualPercentageRate(result.getAnnualPercentageRate().toPlainString())
                .setMonthlyPayment(result.getMonthlyPayment().toPlainString())
                .setExpiresAt(result.getExpiresAt().toString())
                .build();
    }

}
