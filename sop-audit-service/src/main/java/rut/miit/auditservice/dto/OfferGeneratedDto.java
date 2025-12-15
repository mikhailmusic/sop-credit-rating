package rut.miit.auditservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public record OfferGeneratedDto(
        Long totalOffers,
        BigDecimal averageApprovedAmount,
        BigDecimal minApprovedAmount,
        BigDecimal maxApprovedAmount,
        Double averageTermMonths,
        BigDecimal averageAPR,
        BigDecimal minAPR,
        BigDecimal maxAPR,
        BigDecimal averageMonthlyPayment,
        Map<String, Long> productDistribution
) {
}
