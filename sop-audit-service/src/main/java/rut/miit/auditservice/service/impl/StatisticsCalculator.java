package rut.miit.auditservice.service.impl;

import org.springframework.stereotype.Component;
import rut.miit.auditservice.model.*;
import rut.miit.auditservice.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
class StatisticsCalculator {

    public AuditStatisticDto calculate(List<AssessmentRequest> requests, List<AssessmentResponse> responses, List<OfferGenerated> offers) {

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime rangeStart = findEarliestTimestamp(requests, responses, offers);
        OffsetDateTime rangeEnd = findLatestTimestamp(requests, responses, offers);

        return new AuditStatisticDto(
                now,
                rangeStart != null ? rangeStart : now,
                rangeEnd != null ? rangeEnd : now,
                calculateRequestStats(requests),
                calculateResponseStats(responses),
                calculateOfferStats(offers),
                calculateConversionMetrics(requests, responses, offers)
        );
    }

    public AuditStatisticDto createEmpty() {
        OffsetDateTime now = OffsetDateTime.now();

        return new AuditStatisticDto(
                now, now, now,
                new AssessmentRequestDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        0.0, 0.0, BigDecimal.ZERO, Map.of(), Map.of(), 0.0, 0.0, 0.0),
                new AssessmentResponseDto(0L, 0L, 0L, 0.0, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, Map.of(), Map.of()),
                new OfferGeneratedDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        0.0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, Map.of()),
                new ConversionMetricsDto(0.0, 0.0, 0.0)
        );
    }

    private AssessmentRequestDto calculateRequestStats(List<AssessmentRequest> records) {
        if (records.isEmpty()) {
            return new AssessmentRequestDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0.0, 0.0, BigDecimal.ZERO, Map.of(), Map.of(), 0.0, 0.0, 0.0);
        }

        List<BigDecimal> amounts = records.stream().map(AssessmentRequest::getAmount).toList();
        List<Integer> terms = records.stream().map(AssessmentRequest::getTermMonths).toList();
        List<Integer> ages = records.stream().map(AssessmentRequest::getAge).toList();
        List<BigDecimal> incomes = records.stream().map(AssessmentRequest::getAnnualIncome).toList();
        List<Integer> approvedCounts = records.stream().map(AssessmentRequest::getHistoryApprovedCount).toList();
        List<Integer> rejectedCounts = records.stream().map(AssessmentRequest::getHistoryRejectedCount).toList();
        List<Integer> delayedPayments = records.stream().map(AssessmentRequest::getPaymentsDelayedLast12m).toList();

        Map<String, Long> purposeDist = records.stream()
                .collect(Collectors.groupingBy(AssessmentRequest::getPurpose, Collectors.counting()));

        Map<String, Long> employmentDist = records.stream()
                .collect(Collectors.groupingBy(AssessmentRequest::getEmploymentStatus, Collectors.counting()));

        return new AssessmentRequestDto(
                (long) records.size(), calculateAverage(amounts), calculateMin(amounts), calculateMax(amounts),
                calculateAverageInt(terms), calculateAverageInt(ages), calculateAverage(incomes), purposeDist,
                employmentDist, calculateAverageInt(approvedCounts), calculateAverageInt(rejectedCounts),
                calculateAverageInt(delayedPayments)
        );
    }

    private AssessmentResponseDto calculateResponseStats(List<AssessmentResponse> records) {
        if (records.isEmpty()) {
            return new AssessmentResponseDto(0L, 0L, 0L, 0.0, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, Map.of(), Map.of());
        }

        long total = records.size();
        long approved = records.stream().filter(AssessmentResponse::getApproved).count();
        long rejected = total - approved;
        double approvalRate = (approved * 100.0) / total;

        List<BigDecimal> scores = records.stream().map(AssessmentResponse::getCreditScore).toList();

        Map<String, Long> riskDist = records.stream()
                .collect(Collectors.groupingBy(AssessmentResponse::getRiskLevel, Collectors.counting()));

        Map<String, Long> rejectionReasonsDist = records.stream()
                .filter(r -> r.getRejectionReasons() != null && !r.getRejectionReasons().isEmpty())
                .flatMap(r -> Arrays.stream(r.getRejectionReasons().split(";")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        return new AssessmentResponseDto(total, approved, rejected, approvalRate, calculateAverage(scores),
                calculateMin(scores), calculateMax(scores), riskDist, rejectionReasonsDist
        );
    }

    private OfferGeneratedDto calculateOfferStats(List<OfferGenerated> records) {
        if (records.isEmpty()) {
            return new OfferGeneratedDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0.0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, Map.of());
        }

        List<BigDecimal> amounts = records.stream().map(OfferGenerated::getApprovedAmount).toList();
        List<Integer> terms = records.stream().map(OfferGenerated::getTermMonths).toList();
        List<BigDecimal> aprs = records.stream().map(OfferGenerated::getAnnualPercentageRate).toList();
        List<BigDecimal> payments = records.stream().map(OfferGenerated::getMonthlyPayment).toList();

        Map<String, Long> productDist = records.stream()
                .collect(Collectors.groupingBy(OfferGenerated::getProductId, Collectors.counting()));

        return new OfferGeneratedDto((long) records.size(), calculateAverage(amounts), calculateMin(amounts),
                calculateMax(amounts), calculateAverageInt(terms), calculateAverage(aprs), calculateMin(aprs),
                calculateMax(aprs), calculateAverage(payments), productDist
        );
    }

    private ConversionMetricsDto calculateConversionMetrics(List<AssessmentRequest> requests,
                                                            List<AssessmentResponse> responses, List<OfferGenerated> offers) {

        long requestCount = requests.size();
        long approvedCount = responses.stream().filter(AssessmentResponse::getApproved).count();
        long offerCount = offers.size();

        double requestToApproval = requestCount > 0 ? (approvedCount * 100.0 / requestCount) : 0.0;
        double approvalToOffer = approvedCount > 0 ? (offerCount * 100.0 / approvedCount) : 0.0;
        double requestToOffer = requestCount > 0 ? (offerCount * 100.0 / requestCount) : 0.0;

        return new ConversionMetricsDto(requestToApproval, approvalToOffer, requestToOffer);
    }

    private BigDecimal calculateAverage(List<BigDecimal> values) {
        if (values.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private Double calculateAverageInt(List<Integer> values) {
        if (values.isEmpty()) return 0.0;

        double sum = values.stream().mapToInt(Integer::intValue).sum();
        return sum / values.size();
    }

    private BigDecimal calculateMin(List<BigDecimal> values) {
        return values.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMax(List<BigDecimal> values) {
        return values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private OffsetDateTime findEarliestTimestamp(List<AssessmentRequest> requests, List<AssessmentResponse> responses, List<OfferGenerated> offers) {

        OffsetDateTime earliest = null;

        if (!requests.isEmpty()) {
            earliest = requests.stream().map(AssessmentRequest::getAuditTimestamp).min(OffsetDateTime::compareTo).orElse(null);
        }

        if (!responses.isEmpty()) {
            OffsetDateTime respEarliest = responses.stream().map(AssessmentResponse::getAuditTimestamp).min(OffsetDateTime::compareTo).orElse(null);

            if (earliest == null || respEarliest.isBefore(earliest)) {
                earliest = respEarliest;
            }
        }

        if (!offers.isEmpty()) {
            OffsetDateTime offerEarliest = offers.stream().map(OfferGenerated::getAuditTimestamp).min(OffsetDateTime::compareTo).orElse(null);

            if (earliest == null || offerEarliest.isBefore(earliest)) {
                earliest = offerEarliest;
            }
        }

        return earliest;
    }

    private OffsetDateTime findLatestTimestamp(List<AssessmentRequest> requests, List<AssessmentResponse> responses, List<OfferGenerated> offers) {

        OffsetDateTime latest = null;

        if (!requests.isEmpty()) {
            latest = requests.stream().map(AssessmentRequest::getAuditTimestamp).max(OffsetDateTime::compareTo).orElse(null);
        }

        if (!responses.isEmpty()) {
            OffsetDateTime respLatest = responses.stream().map(AssessmentResponse::getAuditTimestamp).max(OffsetDateTime::compareTo).orElse(null);

            if (latest == null || respLatest.isAfter(latest)) {
                latest = respLatest;
            }
        }

        if (!offers.isEmpty()) {
            OffsetDateTime offerLatest = offers.stream().map(OfferGenerated::getAuditTimestamp).max(OffsetDateTime::compareTo).orElse(null);

            if (latest == null || offerLatest.isAfter(latest)) {
                latest = offerLatest;
            }
        }

        return latest;
    }
}
