package rut.miit.auditservice.dto;

public record ConversionMetricsDto(
        Double requestToApprovalRate,
        Double approvalToOfferRate,
        Double requestToOfferRate
) {
}
