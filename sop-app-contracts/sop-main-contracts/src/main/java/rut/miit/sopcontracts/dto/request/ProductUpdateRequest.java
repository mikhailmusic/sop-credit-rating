package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request object for updating an existing credit product")
public record ProductUpdateRequest(

        @Schema(description = "Display name of the product", example = "Updated Consumer Loan")
        @NotBlank(message = "Name is required and cannot be blank")
        @Size(min = 5, max = 255, message = "Name must not exceed 255 characters")
        String name,

        @Schema(description = "Detailed product description", example = "Updated description of consumer credit")
        @Size(min = 15, max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Schema(description = "Version of the product", example = "2")
        @NotNull(message = "Version is required")
        @Min(value = 1, message = "Version must be at least 1")
        Integer version,

        @Schema(description = "Minimum loan amount", example = "5000.00")
        @NotNull(message = "Minimum amount is required")
        @DecimalMin(value = "0.01", message = "Minimum amount must be greater than 0")
        BigDecimal minAmount,

        @Schema(description = "Maximum loan amount", example = "50000.00")
        @NotNull(message = "Maximum amount is required")
        @DecimalMin(value = "0.01", message = "Maximum amount must be greater than 0")
        BigDecimal maxAmount,

        @Schema(description = "Minimum term in months", example = "6")
        @NotNull(message = "Minimum term is required")
        @Min(value = 1, message = "Minimum term must be at least 1 month")
        Integer minTermMonths,

        @Schema(description = "Maximum term in months", example = "60")
        @NotNull(message = "Maximum term is required")
        @Min(value = 1, message = "Maximum term must be at least 1 month")
        Integer maxTermMonths,

        @Schema(description = "Minimum base APR (Annual Percentage Rate)", example = "5.0")
        @NotNull(message = "Base APR minimum is required")
        @DecimalMin(value = "0.0", message = "Base APR minimum must not be negative")
        @DecimalMax(value = "100.00", message = "Base APR minimum must not exceed 100")
        BigDecimal baseAprMin,

        @Schema(description = "Maximum base APR (Annual Percentage Rate)", example = "15.0")
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
