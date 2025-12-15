package rut.miit.auditservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AssessmentRequestDto(
        Long totalRequests,
        BigDecimal averageRequestedAmount,
        BigDecimal minRequestedAmount,
        BigDecimal maxRequestedAmount,
        Double averageTermMonths,
        Double averageAge,
        BigDecimal averageAnnualIncome,
        Map<String, Long> purposeDistribution,
        Map<String, Long> employmentStatusDistribution,
        Double averageHistoryApprovedCount,
        Double averageHistoryRejectedCount,
        Double averagePaymentsDelayed
) {
}
