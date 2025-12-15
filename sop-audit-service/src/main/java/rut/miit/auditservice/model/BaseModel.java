package rut.miit.auditservice.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public abstract class BaseModel {

    private UUID requestId;
    private OffsetDateTime auditTimestamp;

    protected BaseModel() {}

    protected BaseModel(UUID requestId, OffsetDateTime auditTimestamp) {
        this.requestId = requestId;
        this.auditTimestamp = auditTimestamp;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public OffsetDateTime getAuditTimestamp() {
        return auditTimestamp;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setAuditTimestamp(OffsetDateTime auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
    }
}

