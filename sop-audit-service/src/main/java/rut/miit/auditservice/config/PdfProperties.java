package rut.miit.auditservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.reports")
public class PdfProperties {
    private final String baseDir;
    private final String fileExtension;
    private final int retentionDays;

    public PdfProperties(String baseDir, Integer retentionDays) {
        this.baseDir = (baseDir == null || baseDir.isBlank()) ? "./audit-data/reports" : baseDir;
        this.retentionDays = (retentionDays == null || retentionDays <= 0) ? 90 : retentionDays;
        this.fileExtension = ".pdf";
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public int getRetentionDays() {
        return retentionDays;
    }
}
