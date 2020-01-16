package foo.models;

import java.math.BigDecimal;

public class Payment {
    private long sourceId;
    private long targetId;
    private BigDecimal amount;

    public Payment() {
    }

    public Payment(long sourceId, long targetId, BigDecimal amount) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.amount = amount;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{ sourceId: " + sourceId + ", targetId: " + targetId + ", amount: " + amount + "}";
    }
}
