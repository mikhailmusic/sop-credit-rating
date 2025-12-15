package rut.miit.auditservice.service.impl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rut.miit.auditservice.config.CsvProperties;
import rut.miit.auditservice.model.*;
import rut.miit.auditservice.dto.AuditStatisticDto;
import rut.miit.auditservice.storage.csv.AssessmentRequestStorage;
import rut.miit.auditservice.storage.csv.AssessmentResponseStorage;
import rut.miit.auditservice.service.AuditService;
import rut.miit.auditservice.storage.csv.OfferGeneratedStorage;
import rut.miit.sopeventcontracts.assessment.*;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class AuditServiceImpl implements AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AssessmentRequestStorage assessmentRequestStorage;
    private final AssessmentResponseStorage assessmentResponseStorage;
    private final OfferGeneratedStorage offerGeneratedStorage;
    private final StatisticsCalculator calculator;

    private final List<AssessmentRequest> requestsInMemory;
    private final List<AssessmentResponse> responsesInMemory;
    private final List<OfferGenerated> offersInMemory;
    private final ReadWriteLock statsLock;

    private final int daysToLoad;
    private final int retentionDays;

    public AuditServiceImpl(AssessmentRequestStorage assessmentRequestStorage, AssessmentResponseStorage assessmentResponseStorage, OfferGeneratedStorage offerGeneratedStorage, StatisticsCalculator calculator, CsvProperties properties) {
        this.assessmentRequestStorage = assessmentRequestStorage;
        this.assessmentResponseStorage = assessmentResponseStorage;
        this.offerGeneratedStorage = offerGeneratedStorage;
        this.calculator = calculator;
        this.statsLock = new ReentrantReadWriteLock();

        this.requestsInMemory = new ArrayList<>();
        this.responsesInMemory = new ArrayList<>();
        this.offersInMemory = new ArrayList<>();

        this.daysToLoad = properties.getDaysToLoad();
        this.retentionDays = properties.getRetentionDays();
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing audit service - loading statistics from storage");
        loadDataFromStorage();
    }

    private void loadDataFromStorage() {
        statsLock.writeLock().lock();
        try {
            LocalDate from = LocalDate.now().minusDays(daysToLoad);
            LocalDate to = LocalDate.now();

            requestsInMemory.clear();
            responsesInMemory.clear();
            offersInMemory.clear();

            requestsInMemory.addAll(assessmentRequestStorage.findAll(from, to));
            responsesInMemory.addAll(assessmentResponseStorage.findAll(from, to));
            offersInMemory.addAll(offerGeneratedStorage.findAll(from, to));

            log.info("Loaded from storage - Requests: {}, Responses: {}, Offers: {}",
                    requestsInMemory.size(), responsesInMemory.size(), offersInMemory.size());

        } finally {
            statsLock.writeLock().unlock();
        }
    }

    // API

    @Override
    public AuditStatisticDto getCurrentStatistics() {
        statsLock.readLock().lock();
        try {

            return calculator.calculate(requestsInMemory, responsesInMemory, offersInMemory);
        } finally {
            statsLock.readLock().unlock();
        }
    }

    @Override
    public void saveAssessmentRequest(AssessmentRequestEvent event) {
        try {
            AssessmentRequest record = new AssessmentRequest(
                    event.requestId(),
                    OffsetDateTime.now(),
                    event.applicationId().toString(),
                    event.clientId().toString(),
                    event.amount(),
                    event.termMonths(),
                    event.purpose().name(),
                    event.annualIncome(),
                    event.totalMonthlyDebtPayment(),
                    event.employmentStatus().name(),
                    event.age(),
                    event.historyApprovedCount(),
                    event.historyRejectedCount(),
                    event.paymentsDelayedLast12m(),
                    event.maxDaysOverdueLast12m(),
                    event.availableProducts().size()
            );
            assessmentRequestStorage.save(record);      // already has its own lock

            statsLock.writeLock().lock();
            try {
                requestsInMemory.add(record);
            } finally {
                statsLock.writeLock().unlock();
            }

            log.debug("Assessment request processed: {}", event.applicationId());

        } catch (Exception e) {
            log.error("Failed to process assessment request for application: {}", event.applicationId(), e);
        }
    }

    @Override
    public void saveAssessmentResponse(AssessmentCompletedEvent event) {
        try {
            String rejectionReasonsStr = event.rejectionReasons() != null
                    ? String.join(";", event.rejectionReasons())
                    : "";

            AssessmentResponse record = new AssessmentResponse(
                    event.requestId(),
                    OffsetDateTime.now(),
                    event.applicationId().toString(),
                    event.clientId().toString(),
                    event.creditScore(),
                    event.approved(),
                    event.riskLevel().name(),
                    rejectionReasonsStr
            );
            assessmentResponseStorage.save(record);

            statsLock.writeLock().lock();
            try {
                responsesInMemory.add(record);
            } finally {
                statsLock.writeLock().unlock();
            }

            log.debug("Assessment response processed: {}", event.applicationId());

        } catch (Exception e) {
            log.error("Failed to process assessment response for application: {}", event.applicationId(), e);
        }
    }

    @Override
    public void saveOffer(OfferGeneratedEvent event) {
        try {
            OfferGenerated record = new OfferGenerated(
                    event.requestId(),
                    OffsetDateTime.now(),
                    event.clientId().toString(),
                    event.applicationId().toString(),
                    event.productId().toString(),
                    event.approvedAmount(),
                    event.termMonths(),
                    event.annualPercentageRate(),
                    event.monthlyPayment(),
                    event.expiresAt()
            );
            offerGeneratedStorage.save(record);

            statsLock.writeLock().lock();
            try {
                offersInMemory.add(record);
            } finally {
                statsLock.writeLock().unlock();
            }

            log.debug("Offer processed: {}", event.applicationId());

        } catch (Exception e) {
            log.error("Failed to process offer for application: {}", event.applicationId(), e);
        }
    }

    @Override
    public void cleanupOldStats() {
        log.info("Starting cleanup");

        LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
        assessmentRequestStorage.cleanupOlderThan(cutoffDate);
        assessmentResponseStorage.cleanupOlderThan(cutoffDate);
        offerGeneratedStorage.cleanupOlderThan(cutoffDate);

        loadDataFromStorage();

        log.info("Cleanup completed");
    }


    @Override
    public boolean assessmentRequestExists(UUID requestId) {
        return assessmentRequestStorage.exists(requestId);
    }

    @Override
    public boolean assessmentResponseExists(UUID requestId) {
        return assessmentResponseStorage.exists(requestId);
    }

    @Override
    public boolean offerExists(UUID requestId) {
        return offerGeneratedStorage.exists(requestId);
    }

}
