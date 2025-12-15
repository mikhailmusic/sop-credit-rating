package rut.miit.grpc.sopcreditcalc.model;

import rut.miit.grpc.sopcreditcalc.model.enums.Purpose;
import rut.miit.grpc.sopcreditcalc.model.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.List;

public class OfferContext {
    private String requestId;
    private String applicationId;
    private String clientId;
    private BigDecimal creditScore;
    private RiskLevel riskLevel;
    private BigDecimal amount;
    private Integer termMonths;
    private Purpose purpose;
    private List<ProductContext> availableProducts;

    public OfferContext(String requestId, String applicationId, String clientId, BigDecimal creditScore, RiskLevel riskLevel, BigDecimal amount, Integer termMonths, Purpose purpose, List<ProductContext> availableProducts) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.creditScore = creditScore;
        this.riskLevel = riskLevel;
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
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

    public BigDecimal getCreditScore() {
        return creditScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public Purpose getPurpose() {
        return purpose;
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

    public void setCreditScore(BigDecimal creditScore) {
        this.creditScore = creditScore;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public void setAvailableProducts(List<ProductContext> availableProducts) {
        this.availableProducts = availableProducts;
    }
}
