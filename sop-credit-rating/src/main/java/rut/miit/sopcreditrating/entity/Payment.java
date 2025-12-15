package rut.miit.sopcreditrating.entity;

import jakarta.persistence.*;
import rut.miit.sopcreditrating.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    private Offer offer;
    private Client client;
    private BigDecimal amount;
    private PaymentStatus status;
    private String reference;
    private OffsetDateTime dueDate;
    private OffsetDateTime processedAt;

    protected Payment() {}

    public Payment(Offer offer, Client client, BigDecimal amount, OffsetDateTime dueDate){
        this.offer = offer;
        this.client = client;
        this.amount = amount;
        this.status = PaymentStatus.PLANNED;
        this.dueDate = dueDate;
        setActive(true);
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    public Offer getOffer() { return offer; }

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    public Client getClient() { return client;}

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() { return amount; }

    @Column(name = "due_date", nullable = false)
    public OffsetDateTime getDueDate() { return dueDate; }

    @Column(name = "processed_at")
    public OffsetDateTime getProcessedAt() { return processedAt; }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public PaymentStatus getStatus() { return status; }

    @Column(name = "reference", unique = true)
    public String getReference() { return reference; }

    private void setOffer(Offer offer) { this.offer = offer; }
    private void setClient(Client client) { this.client = client; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setReference(String reference) { this.reference = reference; }
    public void setDueDate(OffsetDateTime dueDate) { this.dueDate = dueDate; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }
}

