package rut.miit.auditservice.service;

import rut.miit.auditservice.dto.AuditStatisticDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    void generateDailyReport();
    AuditStatisticDto generateCustomReport(LocalDate from, LocalDate to);
    List<String> listDailyReports();
    Optional<byte[]> getReport(String fileName);
    void cleanupOldReports();
}
