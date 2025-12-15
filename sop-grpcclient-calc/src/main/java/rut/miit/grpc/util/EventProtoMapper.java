package rut.miit.grpc.util;

import org.springframework.stereotype.Component;
import rut.miit.grpc.*;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;
import rut.miit.sopeventcontracts.assessment.ProductSnapshotEvent;
import rut.miit.sopeventcontracts.assessment.RiskLevelEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class EventProtoMapper {

    public AssessmentRequest toGrpcAssessmentRequest(AssessmentRequestEvent event) {
        AssessmentRequest assessmentRequest = AssessmentRequest.newBuilder()
                .setRequestId(event.requestId().toString())
                .setApplicationId(event.applicationId().toString())
                .setClientId(event.clientId().toString())
                .setAmount(event.amount().toPlainString())
                .setTermMonths(event.termMonths())
                .setPurpose(EnumPurpose.valueOf(event.purpose().name()))
                .setAnnualIncome(event.annualIncome().toPlainString())
                .setTotalMonthlyDebtPayment(event.totalMonthlyDebtPayment().toPlainString())
                .setEmploymentStatus(EnumEmploymentStatus.valueOf(event.employmentStatus().name()))
                .setAge(event.age())
                .setHistoryApprovedCount(event.historyApprovedCount())
                .setHistoryRejectedCount(event.historyRejectedCount())
                .setPaymentsDelayedLast12M(event.paymentsDelayedLast12m())
                .setMaxDaysOverdueLast12M(event.maxDaysOverdueLast12m())
                .addAllAvailableProducts(event.availableProducts().stream().map(this::toGrpcProductSnapshot).toList())
                .build();

        return assessmentRequest;
    }

    private ProductSnapshot toGrpcProductSnapshot(ProductSnapshotEvent event) {
        ProductSnapshot snapshot = ProductSnapshot.newBuilder()
                .setProductId(event.productId().toString())
                .setPurpose(EnumPurpose.valueOf(event.purpose().name()))
                .setMinAmount(event.minAmount().toPlainString())
                .setMaxAmount(event.maxAmount().toPlainString())
                .setMinTermMonths(event.minTermMonths())
                .setMaxTermMonths(event.maxTermMonths())
                .setBaseAprMin(event.baseAprMin().toPlainString())
                .setBaseAprMax(event.baseAprMax().toPlainString())
                .build();

        return snapshot;
    }

    public OfferRequest toGrpcOfferRequest(AssessmentRequestEvent original, AssessmentCompletedEvent assessment) {

        OfferRequest offerRequest = OfferRequest.newBuilder()
                .setRequestId(original.requestId().toString())
                .setApplicationId(original.applicationId().toString())
                .setClientId(original.clientId().toString())
                .setCreditScore(assessment.creditScore().toPlainString())
                .setRiskLevel(EnumRiskLevel.valueOf(assessment.riskLevel().name()))
                .setAmount(original.amount().toPlainString())
                .setTermMonths(original.termMonths())
                .setPurpose(EnumPurpose.valueOf(original.purpose().name()))
                .addAllAvailableProducts(original.availableProducts().stream().map(this::toGrpcProductSnapshot).toList())
                .build();

        return offerRequest;
    }

    public AssessmentCompletedEvent toAssessmentCompletedEvent(AssessmentResponse response) {

        AssessmentCompletedEvent assessment = new AssessmentCompletedEvent(
                UUID.fromString(response.getRequestId()),
                UUID.fromString(response.getApplicationId()),
                UUID.fromString(response.getClientId()),
                new BigDecimal(response.getCreditScore()),
                response.getApproved(),
                RiskLevelEvent.valueOf(response.getRiskLevel().name()),
                response.getRejectionReasonsList()
        );
        return assessment;
    }

    public OfferGeneratedEvent toOfferGeneratedEvent(OfferResponse response) {

        OfferGeneratedEvent offer = new OfferGeneratedEvent(
                UUID.fromString(response.getRequestId()),
                UUID.fromString(response.getClientId()),
                UUID.fromString(response.getApplicationId()),
                UUID.fromString(response.getProductId()),
                new BigDecimal(response.getApprovedAmount()),
                response.getTermMonths(),
                new BigDecimal(response.getAnnualPercentageRate()),
                new BigDecimal(response.getMonthlyPayment()),
                OffsetDateTime.parse(response.getExpiresAt())
        );
        return offer;
    }
}
