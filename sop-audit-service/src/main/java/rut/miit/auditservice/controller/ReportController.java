package rut.miit.auditservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.auditservice.dto.AuditStatisticDto;
import rut.miit.auditservice.service.ReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportService reportService;

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<String> listDailyReports() {
        return reportService.listDailyReports();
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> downloadDailyReport(@PathVariable String fileName) {

        Optional<byte[]> fileContent = reportService.getReport(fileName);
        if (fileContent.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
            headers.setContentLength(fileContent.get().length);

            return ResponseEntity.ok().headers(headers).body(fileContent.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/custom")
    public ResponseEntity<AuditStatisticDto> generateCustomReport(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

        AuditStatisticDto statistics = reportService.generateCustomReport(from, to);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping
    public String triggerDailyReportGeneration() {
        reportService.generateDailyReport();
        return "Daily report generation triggered successfully";
    }
}
