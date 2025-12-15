package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Represents a payment record associated with an offer")
@Relation(collectionRelation = "payments", itemRelation = "payment")
public class PaymentResponse extends RepresentationModel<PaymentResponse> {

    private final UUID id;
    private final UUID offerId;
    private final UUID clientId;
    private final BigDecimal amount;
    private final String status;
    private final String reference;
    private final OffsetDateTime dueDate;
    private final OffsetDateTime processedAt;
    private final boolean active;

    public PaymentResponse(UUID id, UUID offerId, UUID clientId, BigDecimal amount, String status, String reference, OffsetDateTime dueDate, OffsetDateTime processedAt, boolean active) {
        this.id = id;
        this.offerId = offerId;
        this.clientId = clientId;
        this.amount = amount;
        this.status = status;
        this.reference = reference;
        this.dueDate = dueDate;
        this.processedAt = processedAt;
        this.active = active;
    }


    @Schema(description = "Unique identifier of the payment")
    public UUID getId() { return id; }

    @Schema(description = "Identifier of the offer associated with this payment")
    public UUID getOfferId() { return offerId; }

    @Schema(description = "Identifier of the client associated with this payment")
    public UUID getClientId() { return clientId; }

    @Schema(description = "Payment amount", example = "1500.00")
    public BigDecimal getAmount() { return amount; }

    @Schema(description = "Current payment status", example = "COMPLETED",
            allowableValues = {"PLANNED", "COMPLETED", "FAILED", "DELAYED", "CANCELED"})
    public String getStatus() { return status; }

    @Schema(description = "Payment reference code", example = "PAY-2025-00123")
    public String getReference() { return reference; }

    @Schema(description = "Planned payment due date", example = "2025-09-29T14:30:00+03:00")
    public OffsetDateTime getDueDate() { return dueDate; }

    @Schema(description = "Date and time when the payment was processed or updated", example = "2025-09-22T14:30:00+03:00")
    public OffsetDateTime getProcessedAt() { return processedAt; }

    @Schema(description = "Whether the payment is active (logical deletion flag)")
    public boolean isActive() { return active; }
}

