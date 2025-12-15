package rut.miit.auditservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.csv")
public class CsvProperties {
    private final String baseDir;
    private final long maxFileSizeMb;
    private final int retentionDays;
    private final int daysToLoad;

    public CsvProperties(String baseDir, Long maxFileSizeMb, Integer retentionDays, Integer daysToLoad) {
        this.baseDir = (baseDir == null || baseDir.isBlank()) ? "./audit-data/csv" : baseDir;
        this.maxFileSizeMb = (maxFileSizeMb == null || maxFileSizeMb <= 0) ? 100 : maxFileSizeMb;
        this.retentionDays = (retentionDays == null || retentionDays <= 0) ? 30 : retentionDays;
        this.daysToLoad = (daysToLoad == null || daysToLoad <= 0) ? 7 : daysToLoad;
    }
    public String getBaseDir() {
        return baseDir;
    }

    public long getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public int getDaysToLoad() {
        return daysToLoad;
    }
}
