package rut.miit.sopeventcontracts.assessment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AssessmentRequestEvent(

        @NotNull(message = "Request ID is required")
        UUID requestId,

        @NotNull(message = "Application ID is required")
        UUID applicationId,

        @NotNull(message = "Client ID is required")
        UUID clientId,

        // Client financial data
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Term is required")
        @Min(value = 1, message = "Term must be at least 1 month")
        @Max(value = 360, message = "Term must not exceed 360 day")
        Integer termMonths,

        @NotNull(message = "Purpose is required and cannot be blank")
        PurposeEvent purpose,

        // client financial snapshot (Client)
        @NotNull(message = "Annual income is required")
        @Positive(message = "Annual income must be greater than 0")
        BigDecimal annualIncome,

        @NotNull(message = "Monthly debt payment is required")
        @PositiveOrZero(message = "Monthly debt payment must be greater than or equal to 0")
        BigDecimal totalMonthlyDebtPayment,

        @NotNull(message = "Employment status is required and cannot be blank")
        EmploymentStatusEvent employmentStatus,

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "Age must be at least 18")
        @Max(value = 100, message = "Age must not exceed 100")
        Integer age,

        // Credit history aggregates
        @NotNull(message = "Delayed payments count is required")
        @Min(value = 0, message = "Approved count must be >= 0")
        Integer historyApprovedCount,

        @NotNull(message = "Delayed payments count is required")
        @Min(value = 0, message = "Rejected count must be >= 0")
        Integer historyRejectedCount,

        @NotNull(message = "Delayed payments count is required")
        @Min(value = 0, message = "Delayed payments must be >= 0")
        Integer paymentsDelayedLast12m,

        @NotNull(message = "Delayed payments count is required")
        @Min(value = 0, message = "Max days overdue must be >= 0")
        Integer maxDaysOverdueLast12m,

        // Available products for this assessment
        @Valid
        @NotEmpty(message = "At least one product is required")
        List<ProductSnapshotEvent> availableProducts
) {}
