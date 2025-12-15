package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Request object for creating a new credit product")
public record ProductRequest(

        @Schema(description = "Unique product code", example = "CONSUMER_STD")
        @NotBlank(message = "Code is required and cannot be blank")
        @Size(min = 5, max = 50, message = "Product code must be between 5 and 50 characters")
        String code,

        @Schema(description = "Display name of the product", example = "Standard Consumer Loan")
        @NotBlank(message = "Name is required and cannot be blank")
        @Size(min = 5, max = 255, message = "Name must not exceed 255 characters")
        String name,

        @Schema(description = "Detailed product description", example = "Basic consumer credit product for personal expenses")
        @Size(min = 15, max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Schema(description = "Version of the product", example = "1")
        @NotNull(message = "Version is required")
        @Min(value = 1, message = "Version must be at least 1")
        Integer version,

        @Schema(description = "Purpose of the credit product", example = "CONSUMER",
                allowableValues = {"CONSUMER", "MORTGAGE", "AUTO", "BUSINESS", "EDUCATION", "MEDICAL",
                        "REFINANCE", "SECURED", "UNSECURED", "OVERDRAFT"})
        @NotBlank(message = "Purpose is required")
        String purpose,

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
