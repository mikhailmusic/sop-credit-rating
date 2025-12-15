package rut.miit.sopcontracts.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Represents a credit product with full details")
@Relation(collectionRelation = "products", itemRelation = "product")
public class ProductResponse extends RepresentationModel<ProductResponse> {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;
    private final Integer version;
    private final String purpose;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final Integer minTermMonths;
    private final Integer maxTermMonths;
    private final BigDecimal baseAprMin;
    private final BigDecimal baseAprMax;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public ProductResponse(UUID id, String code, String name, String description, Integer version, String purpose,
                           BigDecimal minAmount, BigDecimal maxAmount, Integer minTermMonths, Integer maxTermMonths,
                           BigDecimal baseAprMin, BigDecimal baseAprMax, boolean active,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.version = version;
        this.purpose = purpose;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.baseAprMin = baseAprMin;
        this.baseAprMax = baseAprMax;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Schema(description = "Unique identifier of the product")
    public UUID getId() { return id; }

    @Schema(description = "Unique product code (technical identifier)", example = "CONSUMER_STD")
    public String getCode() { return code; }

    @Schema(description = "Display name of the product", example = "Standard Consumer Loan")
    public String getName() { return name; }

    @Schema(description = "Detailed description of the product", example = "Base credit product for individuals")
    public String getDescription() { return description; }

    @Schema(description = "Version of the product", example = "1")
    public Integer getVersion() { return version; }

    @Schema(description = "Purpose of the product", example = "CONSUMER")
    public String getPurpose() { return purpose; }

    @Schema(description = "Minimum loan amount", example = "5000.00")
    public BigDecimal getMinAmount() { return minAmount; }

    @Schema(description = "Maximum loan amount", example = "50000.00")
    public BigDecimal getMaxAmount() { return maxAmount; }

    @Schema(description = "Minimum term in months", example = "6")
    public Integer getMinTermMonths() { return minTermMonths; }

    @Schema(description = "Maximum term in months", example = "60")
    public Integer getMaxTermMonths() { return maxTermMonths; }

    @Schema(description = "Minimum base APR", example = "5.0")
    public BigDecimal getBaseAprMin() { return baseAprMin; }

    @Schema(description = "Maximum base APR", example = "15.0")
    public BigDecimal getBaseAprMax() { return baseAprMax; }

    @Schema(description = "Whether the product is active", example = "true")
    public boolean isActive() { return active; }

    @Schema(description = "Creation timestamp", example = "2025-09-22T14:30:00+03:00")
    public OffsetDateTime getCreatedAt() { return createdAt; }

    @Schema(description = "Last update timestamp", example = "2025-09-25T14:30:00+03:00")
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
