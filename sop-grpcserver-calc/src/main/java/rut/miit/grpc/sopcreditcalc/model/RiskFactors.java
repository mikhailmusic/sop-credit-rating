package rut.miit.grpc.sopcreditcalc.model;

import java.math.BigDecimal;

public class RiskFactors {
    private BigDecimal ageFactor;
    private BigDecimal employmentFactor;
    private BigDecimal purposeFactor;
    private BigDecimal historyFactor;
    private BigDecimal dsrPenalty;
    private BigDecimal ltiPenalty;
    private BigDecimal behaviorFactor;

    public RiskFactors(BigDecimal ageFactor, BigDecimal employmentFactor, BigDecimal purposeFactor, BigDecimal historyFactor, BigDecimal dsrPenalty, BigDecimal ltiPenalty, BigDecimal behaviorFactor) {
        this.ageFactor = ageFactor;
        this.employmentFactor = employmentFactor;
        this.purposeFactor = purposeFactor;
        this.historyFactor = historyFactor;
        this.dsrPenalty = dsrPenalty;
        this.ltiPenalty = ltiPenalty;
        this.behaviorFactor = behaviorFactor;
    }

    public BigDecimal getAgeFactor() {
        return ageFactor;
    }

    public BigDecimal getEmploymentFactor() {
        return employmentFactor;
    }

    public BigDecimal getPurposeFactor() {
        return purposeFactor;
    }

    public BigDecimal getHistoryFactor() {
        return historyFactor;
    }

    public BigDecimal getDsrPenalty() {
        return dsrPenalty;
    }

    public BigDecimal getLtiPenalty() {
        return ltiPenalty;
    }

    public BigDecimal getBehaviorFactor() {
        return behaviorFactor;
    }

    public void setAgeFactor(BigDecimal ageFactor) {
        this.ageFactor = ageFactor;
    }

    public void setEmploymentFactor(BigDecimal employmentFactor) {
        this.employmentFactor = employmentFactor;
    }

    public void setPurposeFactor(BigDecimal purposeFactor) {
        this.purposeFactor = purposeFactor;
    }

    public void setHistoryFactor(BigDecimal historyFactor) {
        this.historyFactor = historyFactor;
    }

    public void setDsrPenalty(BigDecimal dsrPenalty) {
        this.dsrPenalty = dsrPenalty;
    }

    public void setLtiPenalty(BigDecimal ltiPenalty) {
        this.ltiPenalty = ltiPenalty;
    }

    public void setBehaviorFactor(BigDecimal behaviorFactor) {
        this.behaviorFactor = behaviorFactor;
    }
}
