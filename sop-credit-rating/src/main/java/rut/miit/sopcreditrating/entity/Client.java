package rut.miit.sopcreditrating.entity;

import jakarta.persistence.*;
import rut.miit.sopcreditrating.entity.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    private String cif;
    private String fullName;
    private LocalDate birthDate;
    private String email;
    private BigDecimal annualIncome;
    private BigDecimal totalMonthlyDebtPayment;
    private EmploymentStatus employmentStatus;
    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
    private List<Application> applications;
    private List<Payment> payments;

    protected Client() {}

    public Client(String cif, String fullName, LocalDate birthDate, String email, BigDecimal annualIncome, BigDecimal totalMonthlyDebtPayment, EmploymentStatus employmentStatus) {
        setCif(cif);
        setFullName(fullName);
        setBirthDate(birthDate);
        setEmail(email);
        setAnnualIncome(annualIncome);
        setTotalMonthlyDebtPayment(totalMonthlyDebtPayment);
        setEmploymentStatus(employmentStatus);
        setActive(true);
    }

    @Column(name = "cif", nullable = false, unique = true, length = 30)
    public String getCif() {
        return cif;
    }

    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    @Column(name = "birth_date", nullable = false)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    @Column(name = "annual_income", nullable = false)
    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    @Column(name = "total_monthly_debt_payment", nullable = false)
    public BigDecimal getTotalMonthlyDebtPayment() {
        return totalMonthlyDebtPayment;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    @Column(name = "created_date", nullable = false, updatable = false)
    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    @Column(name = "updated_date", nullable = false)
    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    @OneToMany(mappedBy = "client")
    public List<Application> getApplications() {
        return applications;
    }

    @OneToMany(mappedBy = "client")
    public List<Payment> getPayments() { return payments; }

    private void setCif(String cif) { this.cif = cif; }
    private void setFullName(String fullName) { this.fullName = fullName; }
    private void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setEmail(String email) { this.email = email; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public void setTotalMonthlyDebtPayment(BigDecimal totalMonthlyDebtPayment) { this.totalMonthlyDebtPayment = totalMonthlyDebtPayment; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }
    private void setCreatedDate(OffsetDateTime createdDate) { this.createdDate = createdDate; }
    private void setUpdatedDate(OffsetDateTime updatedDate) { this.updatedDate = updatedDate; }
    private void setApplications(List<Application> applications) { this.applications = applications; }
    private void setPayments(List<Payment> payments) { this.payments = payments; }

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
