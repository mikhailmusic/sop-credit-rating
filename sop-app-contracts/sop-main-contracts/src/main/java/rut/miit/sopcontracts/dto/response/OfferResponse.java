package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a credit offer")
@Relation(collectionRelation = "offers", itemRelation = "offer")
public class OfferResponse extends RepresentationModel<OfferResponse> {

    private final UUID id;
    private final UUID applicationId;
    private final UUID productId;
    private final BigDecimal approvedAmount;
    private final Integer termMonths;
    private final BigDecimal apr;
    private final BigDecimal monthlyPayment;
    private final OffsetDateTime expiresAt;
    private final String status;
    private final boolean active;

    public OfferResponse(UUID id, UUID applicationId, UUID productId, BigDecimal approvedAmount,
                         Integer termMonths, BigDecimal apr, BigDecimal monthlyPayment,
                         OffsetDateTime expiresAt, String status, boolean active) {
        this.id = id;
        this.applicationId = applicationId;
        this.productId = productId;
        this.approvedAmount = approvedAmount;
        this.termMonths = termMonths;
        this.apr = apr;
        this.monthlyPayment = monthlyPayment;
        this.expiresAt = expiresAt;
        this.status = status;
        this.active = active;
    }

    @Schema(description = "Unique identifier of the offer")
    public UUID getId() { return id; }

    @Schema(description = "Associated application ID")
    public UUID getApplicationId() { return applicationId; }

    @Schema(description = "Associated product ID")
    public UUID getProductId() { return productId; }

    @Schema(description = "Approved loan amount", example = "15000.00")
    public BigDecimal getApprovedAmount() { return approvedAmount; }

    @Schema(description = "Loan term in months", example = "24")
    public Integer getTermMonths() { return termMonths; }

    @Schema(description = "Annual interest rate (APR)", example = "7.5")
    public BigDecimal getApr() { return apr; }

    @Schema(description = "Monthly payment amount", example = "700.00")
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }

    @Schema(description = "Offer expiration date", example = "2025-09-22T14:30:00+03:00")
    public OffsetDateTime getExpiresAt() { return expiresAt; }

    @Schema(description = "Current status of the offer", example = "CREATED",
            allowableValues = {"CREATED", "ACCEPTED", "REJECTED", "EXPIRED", "CANCELED"})
    public String getStatus() { return status; }

    @Schema(description = "Whether the offer is active (logical deletion flag)")
    public boolean isActive() { return active; }
}

