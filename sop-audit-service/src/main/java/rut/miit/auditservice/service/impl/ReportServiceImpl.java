package rut.miit.auditservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rut.miit.auditservice.config.PdfProperties;
import rut.miit.auditservice.dto.AuditStatisticDto;
import rut.miit.auditservice.model.AssessmentRequest;
import rut.miit.auditservice.model.AssessmentResponse;
import rut.miit.auditservice.model.OfferGenerated;
import rut.miit.auditservice.model.FileInfo;
import rut.miit.auditservice.service.ReportService;
import rut.miit.auditservice.storage.csv.AssessmentRequestStorage;
import rut.miit.auditservice.storage.csv.AssessmentResponseStorage;
import rut.miit.auditservice.storage.csv.OfferGeneratedStorage;
import rut.miit.auditservice.storage.file.PdfReportStorage;
import rut.miit.auditservice.util.PdfReportGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    private static final String DAILY_PREFIX = "daily-report-";
    private static final String CUSTOM_PREFIX = "custom-report-";
    private static final String EXTENSION = ".pdf";

    private final StatisticsCalculator calculator;
    private final PdfReportGenerator pdfGenerator;
    private final PdfReportStorage reportStorage;
    private final AssessmentRequestStorage assessmentRequestStorage;
    private final AssessmentResponseStorage assessmentResponseStorage;
    private final OfferGeneratedStorage offerGeneratedStorage;
    private final int retentionDays;


    public ReportServiceImpl(PdfReportStorage reportStorage, StatisticsCalculator calculator, PdfReportGenerator pdfGenerator, AssessmentRequestStorage assessmentRequestStorage, AssessmentResponseStorage assessmentResponseStorage, OfferGeneratedStorage offerGeneratedStorage, PdfProperties properties) {
        this.reportStorage = reportStorage;
        this.calculator = calculator;
        this.pdfGenerator = pdfGenerator;
        this.assessmentRequestStorage = assessmentRequestStorage;
        this.assessmentResponseStorage = assessmentResponseStorage;
        this.offerGeneratedStorage = offerGeneratedStorage;
        this.retentionDays = properties.getRetentionDays();
    }

    @Override
    public void generateDailyReport() {
        log.info("Generating daily PDF report...");

        try {
            LocalDate today = LocalDate.now();

            List<AssessmentRequest> requests = assessmentRequestStorage.findAll(today, today);
            List<AssessmentResponse> responses = assessmentResponseStorage.findAll(today, today);
            List<OfferGenerated> offers = offerGeneratedStorage.findAll(today, today);

            AuditStatisticDto dailyStats = calculator.calculate(requests, responses, offers);
            byte[] pdfContent = pdfGenerator.generate(dailyStats);

            reportStorage.save(DAILY_PREFIX + today + EXTENSION, pdfContent);

            log.info("Daily PDF report generated");
        } catch (Exception e) {
            log.error("Failed to generate daily PDF report", e);
        }
    }

    @Override
    public AuditStatisticDto generateCustomReport(LocalDate from, LocalDate to) {
        log.info("Generating custom report from {} to {} (with PDF save)", from, to);
        try {
            List<AssessmentRequest> requests = assessmentRequestStorage.findAll(from, to);
            List<AssessmentResponse> responses = assessmentResponseStorage.findAll(from, to);
            List<OfferGenerated> offers = offerGeneratedStorage.findAll(from, to);

            log.info("Loaded data - Requests: {}, Responses: {}, Offers: {}",
                    requests.size(), responses.size(), offers.size());

            AuditStatisticDto statistics = calculator.calculate(requests, responses, offers);

            byte[] pdfContent = pdfGenerator.generate(statistics);

            reportStorage.save(CUSTOM_PREFIX + from + "-to-" + to + EXTENSION, pdfContent);

            log.info("Custom PDF report generated and saved");
            return statistics;
        } catch (Exception e) {
            log.error("Failed to generate custom report from {} to {}", from, to, e);
            throw new RuntimeException("Failed to generate custom report", e);
        }
    }
    @Override
    public List<String> listDailyReports() {
        return reportStorage.findAllWithMetadata().stream().map(FileInfo::fileName).toList();
    }

    @Override
    public Optional<byte[]> getReport(String fileName) {
        return reportStorage.findByFileName(fileName);
    }

    @Override
    public void cleanupOldReports() {
        LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
        reportStorage.cleanupOlderThan(cutoffDate);
    }
}
