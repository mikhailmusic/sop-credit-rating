package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request object for creating a new application, containing all necessary information")
public record ApplicationRequest(
        @Schema(description = "The amount of the application", example = "10000.00")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        @Schema(description = "The purpose of the application", example = "MORTGAGE",
                allowableValues = {"CONSUMER", "MORTGAGE", "AUTO", "BUSINESS", "EDUCATION", "MEDICAL", "REFINANCE", "SECURED", "UNSECURED", "OVERDRAFT"})
        @NotBlank(message = "Purpose is required and cannot be blank")
        String purpose,

        @Schema(description = "The term (duration) of the application in days", example = "5")
        @NotNull(message = "Term is required")
        @Min(value = 1, message = "Term must be at least 1 day")
        @Max(value = 360, message = "Term must not exceed 360 day")
        Integer term,

        @Schema(description = "The ID of the client making the application")
        @NotNull(message = "ClientId is required")
        UUID clientId

) {}
