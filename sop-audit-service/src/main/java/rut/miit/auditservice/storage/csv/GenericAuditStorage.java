package rut.miit.auditservice.storage.csv;

import rut.miit.auditservice.model.BaseModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


interface GenericAuditStorage<T extends BaseModel> {
    void save(T record);
    List<T> findAll(LocalDate from, LocalDate to);
    boolean exists(UUID requestId);
    void cleanupOlderThan(LocalDate cutoffDate);
    long getStorageSize();
    int getFileCount();
    void monitorStorage();
}
