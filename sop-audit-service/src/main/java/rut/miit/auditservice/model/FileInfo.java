package rut.miit.auditservice.model;

import java.time.LocalDateTime;

public record FileInfo(String fileName, long size, LocalDateTime createdAt, LocalDateTime modifiedAt) {}