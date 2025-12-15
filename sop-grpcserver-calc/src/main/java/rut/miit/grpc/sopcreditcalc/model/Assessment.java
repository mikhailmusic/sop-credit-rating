package rut.miit.grpc.sopcreditcalc.model;

import rut.miit.grpc.sopcreditcalc.model.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.List;

public class Assessment {
    private String requestId;
    private String applicationId;
    private String clientId;
    private BigDecimal creditScore;
    private Boolean approved;
    private RiskLevel riskLevel;
    private List<String> rejectionReasons;

    public Assessment(String requestId, String applicationId, String clientId, BigDecimal creditScore, Boolean approved, RiskLevel riskLevel, List<String> rejectionReasons) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.creditScore = creditScore;
        this.approved = approved;
        this.riskLevel = riskLevel;
        this.rejectionReasons = rejectionReasons;
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

    public Boolean getApproved() {
        return approved;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
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

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setRejectionReasons(List<String> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }
}
