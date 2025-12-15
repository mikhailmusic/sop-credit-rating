package rut.miit.auditservice.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AssessmentResponse extends BaseModel {

    private String applicationId;
    private String clientId;
    private BigDecimal creditScore;
    private Boolean approved;
    private String riskLevel;
    private String rejectionReasons;

    protected AssessmentResponse() {
        super();
    }

    public AssessmentResponse(UUID requestId, OffsetDateTime auditTimestamp, String applicationId, String clientId, BigDecimal creditScore, Boolean approved, String riskLevel, String rejectionReasons) {
        super(requestId, auditTimestamp);
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.creditScore = creditScore;
        this.approved = approved;
        this.riskLevel = riskLevel;
        this.rejectionReasons = rejectionReasons;
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
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

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }
}
