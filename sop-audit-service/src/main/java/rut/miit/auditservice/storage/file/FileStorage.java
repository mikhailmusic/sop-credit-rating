package rut.miit.auditservice.storage.file;

import rut.miit.auditservice.model.FileInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


interface FileStorage {
    void save(String fileName, byte[] content);
    Optional<byte[]> findByFileName(String fileName);
    List<FileInfo> findAllWithMetadata();
    boolean delete(String fileName);
    boolean exists(String fileName);
    long getTotalSize();
    int getFileCount();
    void cleanupOlderThan(LocalDate cutoffDate);
}
