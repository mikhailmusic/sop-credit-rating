package rut.miit.sopeventcontracts.assessment;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSnapshotEvent(

        @NotNull(message = "Request ID is required")
        UUID requestId,

        @NotNull(message = "Product ID is required")
        UUID productId,

        @NotBlank(message = "Code is required and cannot be blank")
        @Size(min = 5, max = 50, message = "Product code must be between 5 and 50 characters")
        String code,

        @NotNull(message = "Purpose is required")
        PurposeEvent purpose,

        @NotNull(message = "Minimum amount is required")
        @DecimalMin(value = "0.01", message = "Minimum amount must be greater than 0")
        BigDecimal minAmount,

        @NotNull(message = "Maximum amount is required")
        @DecimalMin(value = "0.01", message = "Maximum amount must be greater than 0")
        BigDecimal maxAmount,

        @NotNull(message = "Minimum term is required")
        @Min(value = 1, message = "Minimum term must be at least 1 month")
        Integer minTermMonths,

        @NotNull(message = "Maximum term is required")
        @Min(value = 1, message = "Maximum term must be at least 1 month")
        Integer maxTermMonths,

        @NotNull(message = "Base APR minimum is required")
        @DecimalMin(value = "0.0", message = "Base APR minimum must not be negative")
        @DecimalMax(value = "100.00", message = "Base APR minimum must not exceed 100")
        BigDecimal baseAprMin,

        @NotNull(message = "Base APR maximum is required")
        @DecimalMin(value = "0.0", message = "Base APR maximum must not be negative")
        @DecimalMax(value = "100.00", message = "Base APR maximum must not exceed 100")
        BigDecimal baseAprMax
) {
        @AssertTrue(message = "minAmount must be less than or equal to maxAmount")
        public boolean isAmountRangeValid() {
                return minAmount == null || maxAmount == null || minAmount.compareTo(maxAmount) <= 0;
        }

        @AssertTrue(message = "minTermMonths must be less than or equal to maxTermMonths")
        public boolean isTermRangeValid() {
                return minTermMonths == null || maxTermMonths == null || minTermMonths <= maxTermMonths;
        }

        @AssertTrue(message = "baseAprMin must be less than or equal to baseAprMax")
        public boolean isAprRangeValid() {
                return baseAprMin == null || baseAprMax == null || baseAprMin.compareTo(baseAprMax) <= 0;
        }
}

