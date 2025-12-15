package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


@Schema(description = "Request object for updating an existing client with new details")
public record ClientUpdateRequest(
        @Schema(description = "The email address of the client", example = "ivan@example.com")
        @NotBlank(message = "Email is required and cannot be blank")
        @Email(message = "Email must be a valid address")
        String email,

        @Schema(description = "The annual income of the client", example = "100000.00")
        @NotNull(message = "Annual income is required")
        @Positive(message = "Annual income must be greater than 0")
        BigDecimal annualIncome,

        @Schema(description = "The total monthly debt payment of the client", example = "1000.00")
        @NotNull(message = "Monthly debt payment is required")
        @Positive(message = "Monthly debt payment must be greater than 0")
        BigDecimal totalMonthlyDebtPayment,

        @Schema(description = "The employment status of the client", example = "EMPLOYED",
                allowableValues = {"EMPLOYED", "SELF_EMPLOYED", "UNEMPLOYED"})
        @NotBlank(message = "Employment status is required and cannot be blank")
        String employmentStatus

) {}
