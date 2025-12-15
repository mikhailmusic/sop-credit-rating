package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Client credit statistics and history")
@Relation(collectionRelation = "statistics", itemRelation = "clientStatistics")
public class ClientStatisticsResponse extends RepresentationModel<ClientStatisticsResponse> {

    private final UUID clientId;

    private final Integer historyApprovedCount;
    private final Integer historyRejectedCount;
    private final Integer totalApplications;
    private final Integer pendingApplications;

    private final BigDecimal annualIncome;
    private final BigDecimal totalMonthlyDebtPayment;
    private final String employmentStatus;

    private final Integer paymentsDelayedLast12m;
    private final Integer maxDaysOverdueLast12m;
    private final Integer totalPaymentsLast12m;
    private final Integer onTimePaymentsLast12m;

    private final Integer activeOffersCount;
    private final BigDecimal totalOutstandingDebt;

    private final Integer clientAge;
    private final OffsetDateTime lastApplicationDate;

    private final OffsetDateTime calculatedAt;

    public ClientStatisticsResponse(UUID clientId, Integer historyApprovedCount, Integer historyRejectedCount, Integer totalApplications, Integer pendingApplications, BigDecimal annualIncome, BigDecimal totalMonthlyDebtPayment, String employmentStatus, Integer paymentsDelayedLast12m, Integer maxDaysOverdueLast12m, Integer totalPaymentsLast12m, Integer onTimePaymentsLast12m, Integer activeOffersCount, BigDecimal totalOutstandingDebt, Integer clientAge, OffsetDateTime lastApplicationDate, OffsetDateTime calculatedAt) {
        this.clientId = clientId;
        this.historyApprovedCount = historyApprovedCount;
        this.historyRejectedCount = historyRejectedCount;
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.annualIncome = annualIncome;
        this.totalMonthlyDebtPayment = totalMonthlyDebtPayment;
        this.employmentStatus = employmentStatus;
        this.paymentsDelayedLast12m = paymentsDelayedLast12m;
        this.maxDaysOverdueLast12m = maxDaysOverdueLast12m;
        this.totalPaymentsLast12m = totalPaymentsLast12m;
        this.onTimePaymentsLast12m = onTimePaymentsLast12m;
        this.activeOffersCount = activeOffersCount;
        this.totalOutstandingDebt = totalOutstandingDebt;
        this.clientAge = clientAge;
        this.lastApplicationDate = lastApplicationDate;
        this.calculatedAt = calculatedAt;
    }


    @Schema(description = "Unique identifier of the client")
    public UUID getClientId() {
        return clientId;
    }

    @Schema(description = "Number of approved applications in history", example = "5")
    public Integer getHistoryApprovedCount() {
        return historyApprovedCount;
    }

    @Schema(description = "Number of rejected applications in history", example = "2")
    public Integer getHistoryRejectedCount() {
        return historyRejectedCount;
    }

    @Schema(description = "Total number of applications submitted", example = "8")
    public Integer getTotalApplications() {
        return totalApplications;
    }

    @Schema(description = "Number of applications currently in PENDING or REVIEWING status", example = "1")
    public Integer getPendingApplications() {
        return pendingApplications;
    }

    @Schema(description = "Annual income of the client in USD", example = "50000.00")
    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    @Schema(description = "Total monthly debt payment in USD", example = "1500.00")
    public BigDecimal getTotalMonthlyDebtPayment() {
        return totalMonthlyDebtPayment;
    }

    @Schema(description = "Employment status", example = "EMPLOYED",
            allowableValues = {"EMPLOYED", "SELF_EMPLOYED", "UNEMPLOYED"})
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    @Schema(description = "Number of delayed or failed payments in the last 12 months", example = "3")
    public Integer getPaymentsDelayedLast12m() {
        return paymentsDelayedLast12m;
    }

    @Schema(description = "Maximum days overdue in the last 12 months", example = "15")
    public Integer getMaxDaysOverdueLast12m() {
        return maxDaysOverdueLast12m;
    }

    @Schema(description = "Total number of payments due in the last 12 months", example = "24")
    public Integer getTotalPaymentsLast12m() {
        return totalPaymentsLast12m;
    }

    @Schema(description = "Number of on-time payments in the last 12 months", example = "21")
    public Integer getOnTimePaymentsLast12m() {
        return onTimePaymentsLast12m;
    }

    @Schema(description = "Number of currently active offers", example = "2")
    public Integer getActiveOffersCount() {
        return activeOffersCount;
    }

    @Schema(description = "Total outstanding debt across all active loans", example = "15000.00")
    public BigDecimal getTotalOutstandingDebt() {
        return totalOutstandingDebt;
    }

    @Schema(description = "Age of the client in years", example = "35")
    public Integer getClientAge() {
        return clientAge;
    }

    @Schema(description = "Timestamp of the most recent application submission")
    public OffsetDateTime getLastApplicationDate() {
        return lastApplicationDate;
    }

    @Schema(description = "Timestamp when these statistics were calculated")
    public OffsetDateTime getCalculatedAt() {
        return calculatedAt;
    }
}