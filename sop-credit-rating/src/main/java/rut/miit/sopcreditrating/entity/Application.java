package rut.miit.sopcreditrating.entity;

import jakarta.persistence.*;
import rut.miit.sopcreditrating.entity.enums.ApplicationStatus;
import rut.miit.sopcreditrating.entity.enums.Purpose;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application extends BaseEntity {

    private BigDecimal amount;
    private Purpose purpose;
    private Integer term;
    private ApplicationStatus applicationStatus;
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
    private Client client;
    private List<Offer> offers;

    protected Application() {}

    public Application(BigDecimal amount, Purpose purpose, Integer term, Client client) {
        this.amount = amount;
        this.purpose = purpose;
        this.term = term;
        this.client = client;
        this.applicationStatus = ApplicationStatus.REVIEWING;
        setActive(true);
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    public Purpose getPurpose() {
        return purpose;
    }

    @Column(name = "term", nullable = false)
    public Integer getTerm() {
        return term;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    @Column(name = "created_date", nullable = false, updatable = false)
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    @Column(name = "updated_date", nullable = false)
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    public Client getClient() {
        return client;
    }

    @OneToMany(mappedBy = "application")
    public List<Offer> getOffers() { return offers; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPurpose(Purpose purpose) { this.purpose = purpose; }
    public void setTerm(Integer term) { this.term = term; }
    public void setApplicationStatus(ApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus;}
    private void setCreatedDate(OffsetDateTime createdDate) { this.createdDate = createdDate; }
    public void setUpdatedDate(OffsetDateTime updatedDate) { this.updatedDate = updatedDate; }
    private void setClient(Client client) { this.client = client; }
    private void setOffers(List<Offer> offers) { this.offers = offers; }

    @PrePersist
    private void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedDate = OffsetDateTime.now();
    }
}
