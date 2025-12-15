package rut.miit.sopcreditrating.entity;

import jakarta.persistence.*;
import rut.miit.sopcreditrating.entity.enums.Purpose;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    private String code;           // Example: CONSUMER_STD
    private String name;
    private String description;
    private Integer version;
    private Purpose purpose;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTermMonths;
    private Integer maxTermMonths;
    private BigDecimal baseAprMin;
    private BigDecimal baseAprMax;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<Offer> offers;

    protected Product() {}

    public Product(String code, String name, String description, Integer version, Purpose purpose, BigDecimal minAmount, BigDecimal maxAmount, Integer minTermMonths, Integer maxTermMonths, BigDecimal baseAprMin, BigDecimal baseAprMax) {
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
        setActive(true);
    }

    @Column(name = "code", nullable = false, unique = true)
    public String getCode() { return code; }

    @Column(name = "name", nullable = false)
    public String getName() { return name; }

    @Column(name = "description")
    public String getDescription() { return description; }

    @Column(name = "version", nullable = false)
    public Integer getVersion() { return version; }

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    public Purpose getPurpose() { return purpose; }

    @Column(name = "min_amount", nullable = false)
    public BigDecimal getMinAmount() { return minAmount; }

    @Column(name = "max_amount", nullable = false)
    public BigDecimal getMaxAmount() { return maxAmount; }

    @Column(name = "min_term_months", nullable = false)
    public Integer getMinTermMonths() { return minTermMonths; }

    @Column(name = "max_term_months", nullable = false)
    public Integer getMaxTermMonths() { return maxTermMonths; }

    @Column(name = "base_apr_min", nullable = false)
    public BigDecimal getBaseAprMin() { return baseAprMin; }

    @Column(name = "base_apr_max", nullable = false)
    public BigDecimal getBaseAprMax() { return baseAprMax; }

    @Column(name = "created_at", nullable = false, updatable = false)
    public OffsetDateTime getCreatedAt() { return createdAt; }

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    @OneToMany(mappedBy = "product")
    public List<Offer> getOffers() { return offers; }

    private void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setVersion(Integer version) { this.version = version; }
    private void setPurpose(Purpose purpose) { this.purpose = purpose; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    public void setMinTermMonths(Integer minTermMonths) { this.minTermMonths = minTermMonths; }
    public void setMaxTermMonths(Integer maxTermMonths) { this.maxTermMonths = maxTermMonths; }
    public void setBaseAprMin(BigDecimal baseAprMin) { this.baseAprMin = baseAprMin; }
    public void setBaseAprMax(BigDecimal baseAprMax) { this.baseAprMax = baseAprMax; }
    private void setOffers(List<Offer> offers) { this.offers = offers; }

    private void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    private void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    private void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}