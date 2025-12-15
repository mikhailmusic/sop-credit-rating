package rut.miit.grpc.sopcreditcalc.model;

import rut.miit.grpc.sopcreditcalc.model.enums.Purpose;

import java.math.BigDecimal;

public class ProductContext {
    private String productId;
    private Purpose purpose;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTermMonths;
    private Integer maxTermMonths;
    private BigDecimal baseAprMin;
    private BigDecimal baseAprMax;

    public ProductContext(String productId, Purpose purpose, BigDecimal minAmount, BigDecimal maxAmount, Integer minTermMonths, Integer maxTermMonths, BigDecimal baseAprMin, BigDecimal baseAprMax) {
        this.productId = productId;
        this.purpose = purpose;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.baseAprMin = baseAprMin;
        this.baseAprMax = baseAprMax;
    }

    public String getProductId() {
        return productId;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public Integer getMinTermMonths() {
        return minTermMonths;
    }

    public Integer getMaxTermMonths() {
        return maxTermMonths;
    }

    public BigDecimal getBaseAprMin() {
        return baseAprMin;
    }

    public BigDecimal getBaseAprMax() {
        return baseAprMax;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMinTermMonths(Integer minTermMonths) {
        this.minTermMonths = minTermMonths;
    }

    public void setMaxTermMonths(Integer maxTermMonths) {
        this.maxTermMonths = maxTermMonths;
    }

    public void setBaseAprMin(BigDecimal baseAprMin) {
        this.baseAprMin = baseAprMin;
    }

    public void setBaseAprMax(BigDecimal baseAprMax) {
        this.baseAprMax = baseAprMax;
    }
}
