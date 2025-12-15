package rut.miit.auditservice.model;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AssessmentRequest extends BaseModel {

    private String applicationId;
    private String clientId;
    private BigDecimal amount;
    private Integer termMonths;
    private String purpose;
    private BigDecimal annualIncome;
    private BigDecimal totalMonthlyDebtPayment;
    private String employmentStatus;
    private Integer age;
    private Integer historyApprovedCount;
    private Integer historyRejectedCount;
    private Integer paymentsDelayedLast12m;
    private Integer maxDaysOverdueLast12m;
    private Integer availableProductsCount;

    protected AssessmentRequest() {
        super();
    }

    public AssessmentRequest(UUID requestId, OffsetDateTime auditTimestamp, String applicationId, String clientId, BigDecimal amount, Integer termMonths, String purpose, BigDecimal annualIncome, BigDecimal totalMonthlyDebtPayment, String employmentStatus, Integer age, Integer historyApprovedCount, Integer historyRejectedCount, Integer paymentsDelayedLast12m, Integer maxDaysOverdueLast12m, Integer availableProductsCount) {
        super(requestId, auditTimestamp);
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.annualIncome = annualIncome;
        this.totalMonthlyDebtPayment = totalMonthlyDebtPayment;
        this.employmentStatus = employmentStatus;
        this.age = age;
        this.historyApprovedCount = historyApprovedCount;
        this.historyRejectedCount = historyRejectedCount;
        this.paymentsDelayedLast12m = paymentsDelayedLast12m;
        this.maxDaysOverdueLast12m = maxDaysOverdueLast12m;
        this.availableProductsCount = availableProductsCount;
    }


    public String getApplicationId() {
        return applicationId;
    }

    public String getClientId() {
        return clientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public BigDecimal getTotalMonthlyDebtPayment() {
        return totalMonthlyDebtPayment;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getHistoryApprovedCount() {
        return historyApprovedCount;
    }

    public Integer getHistoryRejectedCount() {
        return historyRejectedCount;
    }

    public Integer getPaymentsDelayedLast12m() {
        return paymentsDelayedLast12m;
    }

    public Integer getMaxDaysOverdueLast12m() {
        return maxDaysOverdueLast12m;
    }

    public Integer getAvailableProductsCount() {
        return availableProductsCount;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public void setTotalMonthlyDebtPayment(BigDecimal totalMonthlyDebtPayment) {
        this.totalMonthlyDebtPayment = totalMonthlyDebtPayment;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setHistoryApprovedCount(Integer historyApprovedCount) {
        this.historyApprovedCount = historyApprovedCount;
    }

    public void setHistoryRejectedCount(Integer historyRejectedCount) {
        this.historyRejectedCount = historyRejectedCount;
    }

    public void setPaymentsDelayedLast12m(Integer paymentsDelayedLast12m) {
        this.paymentsDelayedLast12m = paymentsDelayedLast12m;
    }

    public void setMaxDaysOverdueLast12m(Integer maxDaysOverdueLast12m) {
        this.maxDaysOverdueLast12m = maxDaysOverdueLast12m;
    }

    public void setAvailableProductsCount(Integer availableProductsCount) {
        this.availableProductsCount = availableProductsCount;
    }
}
