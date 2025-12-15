package rut.miit.auditservice.dto;

import java.time.OffsetDateTime;

public record AuditStatisticDto(
        OffsetDateTime generatedAt,
        OffsetDateTime dataRangeStart,
        OffsetDateTime dataRangeEnd,

        AssessmentRequestDto assessmentRequests,
        AssessmentResponseDto assessmentResponses,
        OfferGeneratedDto offers,
        ConversionMetricsDto conversions
) {
}


