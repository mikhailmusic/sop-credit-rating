package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Represents a client with personal, financial and employment details")
@Relation(collectionRelation = "clients", itemRelation = "client")
public class ClientResponse extends RepresentationModel<ClientResponse> {

    private final UUID id;
    private final String cif;
    private final String fullName;
    private final LocalDate birthDate;
    private final String email;
    private final BigDecimal annualIncome;
    private final BigDecimal totalMonthlyDebtPayment;
    private final String employmentStatus;
    private final boolean active;
    private final OffsetDateTime createdDate;
    private final OffsetDateTime updatedDate;

    public ClientResponse(UUID id, String cif, String fullName, LocalDate birthDate, String email, BigDecimal annualIncome, BigDecimal totalMonthlyDebtPayment, String employmentStatus, boolean active, OffsetDateTime createdDate, OffsetDateTime updatedDate) {
        this.id = id;
        this.cif = cif;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.annualIncome = annualIncome;
        this.totalMonthlyDebtPayment = totalMonthlyDebtPayment;
        this.employmentStatus = employmentStatus;
        this.active = active;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @Schema(description = "Unique identifier of the client")
    public UUID getId() {
        return id;
    }

    @Schema(description = "The CIF (Client Identification Number) of the client", example = "CIF123456")
    public String getCif() {
        return cif;
    }

    @Schema(description = "Full name of the client, including surname, first name and patronymic", example = "Ivanov Ivan Ivanovich")
    public String getFullName() {
        return fullName;
    }

    @Schema(description = "Birth date of the client, must be in the past", example = "2000-01-01")
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Schema(description = "Email address of the client", example = "ivan@example.com")
    public String getEmail() {
        return email;
    }

    @Schema(description = "Annual income of the client in USD", example = "10000.00")
    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    @Schema(description = "Total monthly debt payment of the client in USD", example = "1000.00")
    public BigDecimal getTotalMonthlyDebtPayment() {
        return totalMonthlyDebtPayment;
    }

    @Schema(description = "Current employment status of the client", example = "EMPLOYED",
            allowableValues = {"EMPLOYED", "SELF_EMPLOYED", "UNEMPLOYED"})
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    @Schema(description = "Whether the client is active (logical deletion flag)")
    public boolean isActive() {
        return active;
    }

    @Schema(description = "Date and time when the client record was created", example = "2025-09-22T14:30:00+03:00")
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    @Schema(description = "Date and time when the client record was last updated", example = "2025-09-25T14:30:00+03:00")
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }
}
