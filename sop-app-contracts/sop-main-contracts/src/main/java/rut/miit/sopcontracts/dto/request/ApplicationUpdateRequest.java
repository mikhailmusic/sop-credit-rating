package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request object for creating a new application, containing all necessary information")
public record ApplicationUpdateRequest(
        @Schema(description = "The amount of the application", example = "10000.00")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        @Schema(description = "The term (duration) of the application in days", example = "5")
        @NotNull(message = "Term is required")
        @Min(value = 1, message = "Term must be at least 1 day")
        @Max(value = 360, message = "Term must not exceed 360 day")
        Integer term

) {}
