package rut.miit.auditservice.service;

import rut.miit.auditservice.dto.AuditStatisticDto;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.util.UUID;

public interface AuditService {

    AuditStatisticDto getCurrentStatistics();

    void saveAssessmentRequest(AssessmentRequestEvent event);
    void saveAssessmentResponse(AssessmentCompletedEvent event);
    void saveOffer(OfferGeneratedEvent event);

    void cleanupOldStats();

    boolean assessmentRequestExists(UUID requestId);
    boolean assessmentResponseExists(UUID requestId);
    boolean offerExists(UUID requestId);
}
