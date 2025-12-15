package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request object for creating or updating a client with necessary details")
public record ClientRequest(
        @Schema(description = "The CIF (Client Identification Number) of the client", example = "CIF123456")
        @NotBlank(message = "CIF is required and cannot be blank")
        @Size(min = 6, max = 20, message = "CIF must not exceed 20 characters")
        String cif,

        @Schema(description = "The full name of the client", example = "Ivanov Ivan Ivanovich")
        @NotBlank(message = "Full name is required and cannot be blank")
        @Size(min = 5, max = 255, message = "Full name must not exceed 255 characters")
        String fullName,

        @Schema(description = "The birth date of the client", example = "2000-01-01")
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

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
