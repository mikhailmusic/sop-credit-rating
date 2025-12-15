package rut.miit.sopcreditrating.entity;


import jakarta.persistence.*;
import rut.miit.sopcreditrating.entity.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "offers")
public class Offer extends BaseEntity {

    private Application application;
    private Product product;

    private BigDecimal approvedAmount;
    private Integer termMonths;
    private BigDecimal apr;             // годовая ставка, %
    private BigDecimal monthlyPayment;  // аннуитетный платёж

    private OffsetDateTime expiresAt;
    private OfferStatus status;
    private List<Payment> payments;

    protected Offer() {}

    public Offer(Application application, Product product, BigDecimal approvedAmount, Integer termMonths, BigDecimal apr, BigDecimal monthlyPayment, OffsetDateTime expiresAt) {
        this.application = application;
        this.product = product;
        this.approvedAmount = approvedAmount;
        this.termMonths = termMonths;
        this.apr = apr;
        this.monthlyPayment = monthlyPayment;
        this.expiresAt = expiresAt;
        this.status = OfferStatus.PROPOSED;
        setActive(true);
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    public Application getApplication() { return application; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    public Product getProduct() { return product; }

    @Column(name = "approved_amount", nullable = false)
    public BigDecimal getApprovedAmount() { return approvedAmount; }

    @Column(name = "term_months", nullable = false)
    public Integer getTermMonths() { return termMonths; }

    @Column(name = "apr", nullable = false)
    public BigDecimal getApr() { return apr; }

    @Column(name = "monthly_payment", nullable = false)
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }

    @Column(name = "expires_at", nullable = false)
    public OffsetDateTime getExpiresAt() { return expiresAt; }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public OfferStatus getStatus() { return status; }

    @OneToMany(mappedBy = "offer")
    public List<Payment> getPayments() { return payments; }

    private void setApplication(Application application) { this.application = application; }
    private void setProduct(Product product) { this.product = product; }
    private void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    private void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    private void setApr(BigDecimal apr) { this.apr = apr; }
    private void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    private void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setStatus(OfferStatus status) { this.status = status; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
