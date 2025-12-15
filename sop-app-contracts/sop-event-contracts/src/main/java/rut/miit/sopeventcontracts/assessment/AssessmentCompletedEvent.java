package rut.miit.sopeventcontracts.assessment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AssessmentCompletedEvent(

        @NotNull(message = "Request ID is required")
        UUID requestId,

        @NotNull(message = "Application ID is required")
        UUID applicationId,

        @NotNull(message = "Client ID is required")
        UUID clientId,

        @NotNull(message = "Credit score is required")
        @DecimalMin(value = "0.0", message = "Credit score cannot be negative")
        @DecimalMax(value = "100.0", message = "Credit score cannot exceed 100")
        BigDecimal creditScore,

        boolean approved,

        @NotNull(message = "Risk level is required")
        RiskLevelEvent riskLevel,

        @NotNull(message = "Reasons list cannot be null")
        @Valid
        List<@NotBlank(message = "Reason cannot be blank") String> rejectionReasons
) {}