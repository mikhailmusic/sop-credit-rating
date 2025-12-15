package rut.miit.grpc.sopcreditcalc.model;

import java.math.BigDecimal;

public class FinancialRatios {
    private BigDecimal monthlyIncome;
    private BigDecimal dsrPercent;
    private BigDecimal lti;

    public FinancialRatios(BigDecimal monthlyIncome, BigDecimal dsrPercent, BigDecimal lti) {
        this.monthlyIncome = monthlyIncome;
        this.dsrPercent = dsrPercent;
        this.lti = lti;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getDsrPercent() {
        return dsrPercent;
    }

    public BigDecimal getLti() {
        return lti;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public void setDsrPercent(BigDecimal dsrPercent) {
        this.dsrPercent = dsrPercent;
    }

    public void setLti(BigDecimal lti) {
        this.lti = lti;
    }
}
