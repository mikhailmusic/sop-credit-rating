package rut.miit.sopcreditrating.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.request.ApplicationRequest;
import rut.miit.sopcontracts.dto.request.ApplicationUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.Application;
import rut.miit.sopcreditrating.entity.Client;
import rut.miit.sopcreditrating.entity.Product;
import rut.miit.sopcreditrating.entity.enums.ApplicationStatus;
import rut.miit.sopcreditrating.entity.enums.Purpose;
import rut.miit.sopcreditrating.rabbitmq.AssessmentEventSender;
import rut.miit.sopcreditrating.repository.ApplicationRepository;
import rut.miit.sopcreditrating.repository.ClientRepository;
import rut.miit.sopcreditrating.repository.ProductRepository;
import rut.miit.sopcreditrating.service.ApplicationService;
import rut.miit.sopcreditrating.service.ClientStatisticsService;
import rut.miit.sopcreditrating.service.OfferService;
import rut.miit.sopcreditrating.util.EnumUtils;
import rut.miit.sopeventcontracts.assessment.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);
    private ApplicationRepository applicationRepository;
    private ClientRepository clientRepository;
    private ProductRepository productRepository;
    private OfferService offerService;
    private ClientStatisticsService clientStatisticsService;
    private AssessmentEventSender assessmentEventSender;

    @Override
    public ApplicationResponse createApplication(ApplicationRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .filter(Client::isActive).orElseThrow(() -> new ResourceNotFoundException("Client", request.clientId()));

        Purpose purpose = EnumUtils.parseEnumOrThrow(Purpose.class, request.purpose(), "purpose");

        Application application = new Application(request.amount(), purpose, request.term(), client);
        applicationRepository.save(application);

        ClientStatisticsResponse stats = clientStatisticsService.calculateStatistics(client.getId());
        List<ProductSnapshotEvent> availableProducts = selectProducts(application);

        if (availableProducts.isEmpty()) {
            throw new ResourceNotFoundException("No suitable products found for application " + application.getId());
        }

        AssessmentRequestEvent assessmentRequestEvent = new AssessmentRequestEvent(
                UUID.randomUUID(), application.getId(), application.getClient().getId(), application.getAmount(),
                application.getTerm(), PurposeEvent.valueOf(application.getPurpose().name()), stats.getAnnualIncome(),
                stats.getTotalMonthlyDebtPayment(), EmploymentStatusEvent.valueOf(stats.getEmploymentStatus()),
                stats.getClientAge(), stats.getHistoryApprovedCount(), stats.getHistoryRejectedCount(), stats.getPaymentsDelayedLast12m(),
                stats.getMaxDaysOverdueLast12m(), availableProducts
        );
        assessmentEventSender.sendAssessmentRequest(assessmentRequestEvent);

        return toDto(application);
    }

    @Override
    public ApplicationResponse updateApplication(UUID id, ApplicationUpdateRequest request) {
        Application application = applicationRepository.findById(id)
                .filter(Application::isActive).orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (application.getApplicationStatus() == ApplicationStatus.APPROVED) {
            throw new BusinessLogicException("Approved application cannot be modified");
        }

        if (application.getApplicationStatus() == ApplicationStatus.REJECTED) {
            throw new BusinessLogicException("Rejected application cannot be modified");
        }

        application.setAmount(request.amount());
        application.setTerm(request.term());
        application.setApplicationStatus(ApplicationStatus.REVIEWING);
        applicationRepository.save(application);

        // Recalculate/create another offer - status REVIEWING

        offerService.cancelAllByApplication(id);

        ClientStatisticsResponse stats = clientStatisticsService.calculateStatistics(application.getClient().getId());
        List<ProductSnapshotEvent> availableProducts = selectProducts(application);

        AssessmentRequestEvent assessmentRequestEvent = new AssessmentRequestEvent(
                UUID.randomUUID(), application.getId(), application.getClient().getId(), application.getAmount(),
                application.getTerm(), PurposeEvent.valueOf(application.getPurpose().name()), stats.getAnnualIncome(),
                stats.getTotalMonthlyDebtPayment(), EmploymentStatusEvent.valueOf(stats.getEmploymentStatus()),
                stats.getClientAge(), stats.getHistoryApprovedCount(), stats.getHistoryRejectedCount(), stats.getPaymentsDelayedLast12m(),
                stats.getMaxDaysOverdueLast12m(), availableProducts
        );        assessmentEventSender.sendAssessmentRequest(assessmentRequestEvent);

        return toDto(application);
    }

    @Override
    public ApplicationResponse getApplication(UUID id) {
        Application application = applicationRepository.findById(id)
                .filter(Application::isActive).orElseThrow(() -> new ResourceNotFoundException("Application", id));
        return toDto(application);
    }

    @Override
    public PagedResponse<ApplicationResponse> getAllApplications( int page, int size, String status) {
        boolean active = true;
        return getAllApplications(page, size, status, active);
    }

    @Override
    public PagedResponse<ApplicationResponse> getAllApplications( int page, int size, String status, boolean active) {
        if (page < 0) throw new BusinessLogicException("page must be >= 0");
        if (size <= 0) throw new BusinessLogicException("size must be > 0");

        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        ApplicationStatus parsedStatus = null;
        if (status != null && !status.isBlank()) {
            parsedStatus = EnumUtils.parseEnumOrThrow(ApplicationStatus.class, status, "status");
        }

        Page<Application> pageResult = applicationRepository.findApplications(pageable, parsedStatus, active);

        return new PagedResponse<>(
                pageResult.getContent().stream().map(this::toDto).toList(),
                pageResult.getNumber(), pageResult.getSize(), (int) pageResult.getTotalElements(),
                pageResult.getTotalPages(), pageResult.isLast()
        );
    }

    @Override
    public List<ApplicationResponse> getClientApplications(UUID clientId) {
        boolean active = true;
        return getClientApplications(clientId, active);
    }

    @Override
    public List<ApplicationResponse> getClientApplications(UUID clientId, boolean active) {
        List<Application> applications = applicationRepository.findAllByClientId(clientId);
        return applications.stream().filter(a -> a.isActive() == active).map(this::toDto).toList();
    }

    @Override
    public void handleAssessmentResult(AssessmentCompletedEvent assessment) {
        if (assessment == null || assessment.applicationId() == null) {
            log.error("Assessment payload is invalid: applicationId is required");
            return;
        }

        if (assessment.approved()) { return; }

        UUID applicationId = assessment.applicationId();
        Application application = applicationRepository.findById(applicationId).filter(Application::isActive).orElse(null);

        if (application == null) {
            log.warn("Application {} not found or not active, skipping assessment", applicationId);
            return;
        }

        if (assessment.clientId() != null && !assessment.clientId().equals(application.getClient().getId())) {
            log.error("Assessment clientId does not match application client for {}", applicationId);
            return;
        }

        ApplicationStatus status = application.getApplicationStatus();
        if (status == ApplicationStatus.REJECTED) {
            log.info("Application {} already rejected, skipping duplicate assessment", applicationId);
            return;
        }
        if (status == ApplicationStatus.APPROVED) {
            log.warn("Application {} already approved, cannot reject", applicationId);
            return;
        }
        application.setApplicationStatus(ApplicationStatus.REJECTED);
        applicationRepository.update(application);
    }


    @Override
    public void deleteLogicalApplication(UUID id) {
        Application application = applicationRepository.findById(id)
                .filter(Application::isActive).orElseThrow(() -> new ResourceNotFoundException("Application", id));
        offerService.cancelAllByApplication(id);
        application.setActive(false);
        applicationRepository.save(application);
    }



    @Override
    public List<ApplicationResponse> getByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return applicationRepository.findByIds(ids, active).stream().map(this::toDto).toList();
    }

    @Override
    public List<ApplicationResponse> getByClientIds(Set<UUID> clientIds, boolean active) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        return applicationRepository.findByClientIds(clientIds, active).stream().map(this::toDto).toList();
    }


    private List<ProductSnapshotEvent> selectProducts(Application app) {
        List<Product> products = productRepository.findByPurpose(app.getPurpose(), true);

        BigDecimal amount = app.getAmount();
        Integer termMonths = app.getTerm();

        List<Product> fit = products.stream()
                .filter(p -> amount.compareTo(p.getMinAmount()) >= 0 && amount.compareTo(p.getMaxAmount()) <= 0)
                .filter(p -> termMonths >= p.getMinTermMonths() && termMonths <= p.getMaxTermMonths())
                .sorted(Comparator.comparing(Product::getBaseAprMin))
                .toList();

        List<ProductSnapshotEvent> snapshots = fit.stream()
                .map(p -> new ProductSnapshotEvent(
                        UUID.randomUUID(), p.getId(), p.getCode(), PurposeEvent.valueOf(p.getPurpose().name()),
                        p.getMinAmount(), p.getMaxAmount(), p.getMinTermMonths(), p.getMaxTermMonths(),
                        p.getBaseAprMin(), p.getBaseAprMax()
                ))
                .toList();

        return snapshots;
    }


    private ApplicationResponse toDto(Application a) {
        return new ApplicationResponse(
                a.getId(), a.getAmount(), a.getPurpose().name(), a.getTerm(), a.getApplicationStatus().name(),
                a.getCreatedDate(), a.getUpdatedDate(), a.getClient().getId(), a.isActive()
        );
    }


    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {this.productRepository = productRepository;}

    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    @Autowired
    public void setClientStatisticsService(ClientStatisticsService clientStatisticsService) {this.clientStatisticsService = clientStatisticsService;}

    @Autowired
    public void setAssessmentEventSender(AssessmentEventSender assessmentEventSender) {
        this.assessmentEventSender = assessmentEventSender;
    }
}
