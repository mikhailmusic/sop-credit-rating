package rut.miit.grpc.sopcreditcalc.model;

import rut.miit.grpc.sopcreditcalc.model.enums.EmploymentStatus;
import rut.miit.grpc.sopcreditcalc.model.enums.Purpose;

import java.math.BigDecimal;
import java.util.List;

public class AssessmentContext {
    private String requestId;
    private String applicationId;
    private String clientId;
    private BigDecimal amount;
    private int termMonths;
    private Purpose purpose;
    private BigDecimal annualIncome;
    private BigDecimal totalMonthlyDebtPayment;
    private EmploymentStatus employmentStatus;
    private int age;
    private int historyApprovedCount;
    private int historyRejectedCount;
    private int paymentsDelayedLast12m;
    private int maxDaysOverdueLast12m;
    private List<ProductContext> availableProducts;

    public AssessmentContext(String requestId, String applicationId, String clientId, BigDecimal amount, int termMonths, Purpose purpose, BigDecimal annualIncome, BigDecimal totalMonthlyDebtPayment, EmploymentStatus employmentStatus, int age, int historyApprovedCount, int historyRejectedCount, int paymentsDelayedLast12m, int maxDaysOverdueLast12m, List<ProductContext> availableProducts) {
        this.requestId = requestId;
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
        this.availableProducts = availableProducts;
    }

    public String getRequestId() {
        return requestId;
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

    public int getTermMonths() {
        return termMonths;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public BigDecimal getTotalMonthlyDebtPayment() {
        return totalMonthlyDebtPayment;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public int getAge() {
        return age;
    }

    public int getHistoryApprovedCount() {
        return historyApprovedCount;
    }

    public int getHistoryRejectedCount() {
        return historyRejectedCount;
    }

    public int getPaymentsDelayedLast12m() {
        return paymentsDelayedLast12m;
    }

    public int getMaxDaysOverdueLast12m() {
        return maxDaysOverdueLast12m;
    }

    public List<ProductContext> getAvailableProducts() {
        return availableProducts;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public void setTotalMonthlyDebtPayment(BigDecimal totalMonthlyDebtPayment) {
        this.totalMonthlyDebtPayment = totalMonthlyDebtPayment;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHistoryApprovedCount(int historyApprovedCount) {
        this.historyApprovedCount = historyApprovedCount;
    }

    public void setHistoryRejectedCount(int historyRejectedCount) {
        this.historyRejectedCount = historyRejectedCount;
    }

    public void setPaymentsDelayedLast12m(int paymentsDelayedLast12m) {
        this.paymentsDelayedLast12m = paymentsDelayedLast12m;
    }

    public void setMaxDaysOverdueLast12m(int maxDaysOverdueLast12m) {
        this.maxDaysOverdueLast12m = maxDaysOverdueLast12m;
    }

    public void setAvailableProducts(List<ProductContext> availableProducts) {
        this.availableProducts = availableProducts;
    }
}
