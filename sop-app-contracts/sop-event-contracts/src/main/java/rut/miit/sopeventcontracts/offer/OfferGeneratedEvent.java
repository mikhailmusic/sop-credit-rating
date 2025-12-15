package rut.miit.sopeventcontracts.offer;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OfferGeneratedEvent(

        @NotNull(message = "Request ID is required")
        UUID requestId,

        @NotNull(message = "Client ID is required")
        UUID clientId,

        @NotNull(message = "Application ID is required")
        UUID applicationId,

        @NotNull(message = "Product ID is required")
        UUID productId,

        @NotNull(message = "Approved amount is required")
        @DecimalMin(value = "0.01", message = "Approved amount must be greater than 0")
        BigDecimal approvedAmount,

        @NotNull(message = "Loan term is required")
        @Min(value = 1, message = "Loan term must be at least 1 month")
        @Max(value = 360, message = "Loan term must not exceed 360 months")
        Integer termMonths,

        @NotNull(message = "APR is required")
        @DecimalMin(value = "0.0", message = "APR cannot be negative")
        @DecimalMax(value = "100.0", message = "APR cannot exceed 100%")
        BigDecimal annualPercentageRate,

        @NotNull(message = "Monthly payment is required")
        @DecimalMin(value = "0.01", message = "Monthly payment must be greater than 0")
        BigDecimal monthlyPayment,

        @NotNull(message = "Offer expiration timestamp is required")
        @Future(message = "Offer expiration must be in the future")
        OffsetDateTime expiresAt
) {}
