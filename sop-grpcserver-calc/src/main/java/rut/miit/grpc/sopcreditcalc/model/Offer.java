package rut.miit.grpc.sopcreditcalc.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Offer {
    private String requestId;
    private String clientId;
    private String applicationId;
    private String productId;
    private BigDecimal approvedAmount;
    private Integer termMonths;
    private BigDecimal annualPercentageRate;
    private BigDecimal monthlyPayment;
    private OffsetDateTime expiresAt;

    public Offer(String requestId, String clientId,String applicationId, String productId, BigDecimal approvedAmount, Integer termMonths, BigDecimal annualPercentageRate, BigDecimal monthlyPayment, OffsetDateTime expiresAt) {
        this.requestId = requestId;
        this.clientId = clientId;
        this.applicationId = applicationId;
        this.productId = productId;
        this.approvedAmount = approvedAmount;
        this.termMonths = termMonths;
        this.annualPercentageRate = annualPercentageRate;
        this.monthlyPayment = monthlyPayment;
        this.expiresAt = expiresAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public BigDecimal getAnnualPercentageRate() {
        return annualPercentageRate;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public void setAnnualPercentageRate(BigDecimal annualPercentageRate) {
        this.annualPercentageRate = annualPercentageRate;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
