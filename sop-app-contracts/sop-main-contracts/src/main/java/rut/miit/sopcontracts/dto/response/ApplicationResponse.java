package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Represents a credit application with financial and purpose details")
@Relation(collectionRelation = "applications", itemRelation = "application")
public class ApplicationResponse extends RepresentationModel<ApplicationResponse> {

    private final UUID id;
    private final BigDecimal amount;
    private final String purpose;
    private final Integer term;
    private final String applicationStatus;
    private final OffsetDateTime createdDate;
    private final OffsetDateTime updatedDate;
    private final UUID clientId;
    private final boolean active;

    public ApplicationResponse(UUID id, BigDecimal amount, String purpose, Integer term, String applicationStatus, OffsetDateTime createdDate, OffsetDateTime updatedDate, UUID clientId, boolean active) {
        this.id = id;
        this.amount = amount;
        this.purpose = purpose;
        this.term = term;
        this.applicationStatus = applicationStatus;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.clientId = clientId;
        this.active = active;
    }

    @Schema(description = "Unique identifier of the credit application", example = "123e4567-e89b-12d3-a456-426614174000")
    public UUID getId() {
        return id;
    }

    @Schema(description = "Credit amount requested by the applicant", example = "10000.00")
    public BigDecimal getAmount() {
        return amount;
    }

    @Schema(description = "Purpose of the credit application", example = "MORTGAGE",
            allowableValues = {"CONSUMER", "MORTGAGE", "AUTO", "BUSINESS", "EDUCATION", "MEDICAL", "REFINANCE", "SECURED", "UNSECURED", "OVERDRAFT"})
    public String getPurpose() {
        return purpose;
    }

    @Schema(description = "Term of the credit in months", example = "12")
    public Integer getTerm() {
        return term;
    }

    @Schema(description = "Current status of the credit application", example = "APPROVED",
            allowableValues = {"REVIEWING", "APPROVED", "REJECTED"})
    public String getApplicationStatus() {
        return applicationStatus;
    }

    @Schema(description = "Date and time when the application was created", example = "2025-09-22T14:30:00+03:00")
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    @Schema(description = "Date and time when the application was last updated", example = "2025-10-22T14:30:00+03:00")
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Schema(description = "Unique identifier of the client associated with the application")
    public UUID getClientId() {
        return clientId;
    }

    @Schema(description = "Whether the application is active (logical deletion flag)")
    public boolean isActive() {
        return active;
    }
}
