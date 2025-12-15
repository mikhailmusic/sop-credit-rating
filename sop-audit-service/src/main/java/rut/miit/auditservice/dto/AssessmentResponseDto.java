package rut.miit.auditservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AssessmentResponseDto(
        Long totalResponses,
        Long approvedCount,
        Long rejectedCount,
        Double approvalRate,
        BigDecimal averageCreditScore,
        BigDecimal minCreditScore,
        BigDecimal maxCreditScore,
        Map<String, Long> riskLevelDistribution,
        Map<String, Long> topRejectionReasons
) {
}
