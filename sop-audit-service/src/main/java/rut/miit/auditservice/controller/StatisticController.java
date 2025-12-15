package rut.miit.auditservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.auditservice.dto.AuditStatisticDto;
import rut.miit.auditservice.service.AuditService;


@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    private  AuditService auditService;

    @Autowired
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<AuditStatisticDto> getCurrentStatistics() {
        AuditStatisticDto statistic = auditService.getCurrentStatistics();
        return ResponseEntity.ok(statistic);
    }
}
