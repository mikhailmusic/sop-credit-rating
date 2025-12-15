package rut.miit.auditservice.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rut.miit.auditservice.service.AuditService;
import rut.miit.auditservice.service.ReportService;


@Component
public class AuditScheduler {
    private static final Logger log = LoggerFactory.getLogger(AuditScheduler.class);
    private final AuditService auditService;
    private final ReportService reportService;

    public AuditScheduler(AuditService auditService, ReportService reportService) {
        this.auditService = auditService;
        this.reportService = reportService;
    }

    @Scheduled(cron = "${audit.schedule.file-cleanup}")
    public void cleanupOldCsv() {
        log.info("Scheduled task: Cleanup old CSV files");
        auditService.cleanupOldStats();
        log.info("Cleanup completed");
    }

    @Scheduled(cron = "${audit.schedule.daily-report}")
    public void generateDailyReport() {
        log.info("Scheduled task: Generate daily PDF report");
        reportService.generateDailyReport();
    }

    @Scheduled(cron = "${audit.schedule.report-cleanup}")
    public void cleanupOldReports() {
        log.info("Scheduled task: Cleanup old PDF reports");
        reportService.cleanupOldReports();
    }
}
