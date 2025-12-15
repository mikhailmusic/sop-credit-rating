package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for updating payment status")
public record PaymentStatusUpdateRequest(

        @Schema(description = "The new payment status", example = "COMPLETED",
                allowableValues = {"COMPLETED", "FAILED", "DELAYED", "CANCELED"})
        @NotBlank(message = "Status is required")
        String status,

        @Schema(description = "External reference code (optional, provided by payment system)", example = "TRX-2025-000123")
        @Size(max = 100, message = "Reference must not exceed 100 characters")
        String reference
) {}
