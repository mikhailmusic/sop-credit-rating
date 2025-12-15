package rut.miit.sopcreditrating.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.request.OfferDecisionRequest;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.*;
import rut.miit.sopcreditrating.entity.enums.ApplicationStatus;
import rut.miit.sopcreditrating.entity.enums.OfferStatus;
import rut.miit.sopcreditrating.repository.*;
import rut.miit.sopcreditrating.service.OfferService;
import rut.miit.sopcreditrating.util.EnumUtils;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class OfferServiceImpl implements OfferService {

    private OfferRepository offerRepository;
    private ApplicationRepository applicationRepository;
    private ProductRepository productRepository;
    private PaymentRepository paymentRepository;

    @Override
    public OfferResponse createOffer(OfferGeneratedEvent request) {
        Application app = applicationRepository.findById(request.applicationId())
                .filter(Application::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Application", request.applicationId()));

        if (app.getApplicationStatus() == ApplicationStatus.APPROVED) {
            throw new BusinessLogicException("Cannot create offer: application is already APPROVED");
        }

        Product product = productRepository.findById(request.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.productId()));

        boolean hasAccepted = offerRepository.findByApplicationId(app.getId(), true).stream()
                .anyMatch(o -> o.getStatus() == OfferStatus.ACCEPTED);
        if (hasAccepted) {
            throw new BusinessLogicException("Application already has an ACCEPTED offer");
        }

        if (product.getPurpose() != app.getPurpose()) {
            throw new BusinessLogicException("Product purpose " + product.getPurpose()
                    + " does not match application purpose " + app.getPurpose());
        }

        BigDecimal amount = request.approvedAmount();
        int term = request.termMonths();
        BigDecimal apr = request.annualPercentageRate();

        if (amount.compareTo(product.getMinAmount()) < 0 || amount.compareTo(product.getMaxAmount()) > 0) {
            throw new BusinessLogicException("Approved amount must be within product bounds [" +
                    product.getMinAmount() + " .. " + product.getMaxAmount() + "]");
        }
        if (term < product.getMinTermMonths() || term > product.getMaxTermMonths()) {
            throw new BusinessLogicException("Term must be within product bounds [" +
                    product.getMinTermMonths() + " .. " + product.getMaxTermMonths() + "]");
        }
        if (apr.compareTo(product.getBaseAprMin()) < 0 || apr.compareTo(product.getBaseAprMax()) > 0) {
            throw new BusinessLogicException("APR must be within product bounds [" +
                    product.getBaseAprMin() + " .. " + product.getBaseAprMax() + "]");
        }

        if (request.expiresAt() != null && request.expiresAt().isBefore(OffsetDateTime.now())) {
            throw new BusinessLogicException("Offer expiration time must be in the future");
        }

        OffsetDateTime now = OffsetDateTime.now();

        List<Offer> activeNotExpiredOffers = offerRepository.findByApplicationId(app.getId(), true).stream()
                .filter(o -> o.getStatus() == OfferStatus.PROPOSED)
                .filter(o -> o.getExpiresAt() == null || o.getExpiresAt().isAfter(now))
                .toList();

        boolean duplicate = activeNotExpiredOffers.stream().anyMatch(o -> o.getProduct().getId().equals(product.getId()));

        if (duplicate) {
            throw new BusinessLogicException("Active non-expired PROPOSED offer already exists for this product");
        }

        if (activeNotExpiredOffers.size() >= 3) {
            throw new BusinessLogicException("Maximum number of active non-expired offers (3) reached for application " + app.getId());
        }


        Offer offer = new Offer(app, product, request.approvedAmount(), request.termMonths(), request.annualPercentageRate(),
                request.monthlyPayment(), request.expiresAt());

        offerRepository.save(offer);

        return toDto(offer);
    }

    @Override
    public OfferResponse getOffer(UUID id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer", id));
        return toDto(offer);
    }

    @Override
    public OfferResponse getCurrentByApplication(UUID applicationId) {
        boolean onlyActive = true;

        List<Offer> offers = offerRepository.findByApplicationId(applicationId, onlyActive);
        if (offers.isEmpty()) {
            throw new ResourceNotFoundException("No active offers found for application " + applicationId);
        }

        // Действующий принятый
        List<Offer> accepted = offers.stream().filter(o -> o.getStatus() == OfferStatus.ACCEPTED).toList();
        if (!accepted.isEmpty()) {
            return toDto(accepted.getFirst());
        }

        // Актуальное предложение к выбору
        OffsetDateTime now = OffsetDateTime.now();
        Offer proposed = offers.stream()
                .filter(o -> o.getStatus() == OfferStatus.PROPOSED)
                .filter(o -> o.getExpiresAt() == null || o.getExpiresAt().isAfter(now))
                .max(Comparator.comparing(Offer::getExpiresAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No current (ACCEPTED or valid PROPOSED) offer for application " + applicationId));

        return toDto(proposed);
    }

    @Override
    public List<OfferResponse> getByApplication(UUID applicationId) {
        boolean active = true;
        return getByApplication(applicationId, active);
    }

    @Override
    public List<OfferResponse> getByApplication(UUID applicationId, boolean active) {
        return offerRepository.findByApplicationId(applicationId, active).stream()
                .sorted(Comparator
                        .comparing((Offer o) -> o.getStatus() == OfferStatus.ACCEPTED ? 0 : 1)
                        .thenComparing(Offer::getExpiresAt,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto).toList();
    }

    @Override
    public PagedResponse<OfferResponse> getAllOffers(int page, int size) {
        boolean active = true;
        return getAllOffers(page, size, active);
    }

    @Override
    public PagedResponse<OfferResponse> getAllOffers(int page, int size, boolean active) {
        if (page < 0) throw new BusinessLogicException("page must be >= 0");
        if (size <= 0) throw new BusinessLogicException("size must be > 0");

        Sort sort = Sort.by(Sort.Direction.DESC, "expiresAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Offer> offerPage = offerRepository.findAll(pageable, active);

        return new PagedResponse<>(
                offerPage.getContent().stream().map(this::toDto).toList(),
                offerPage.getNumber(),
                offerPage.getSize(),
                (int) offerPage.getTotalElements(),
                offerPage.getTotalPages(),
                offerPage.isLast()
        );
    }


    @Override
    public List<OfferResponse> getByProduct(UUID productId) {
        boolean active = true;
        return getByProduct(productId, active);
    }

    @Override
    public List<OfferResponse> getByProduct(UUID productId, boolean active) {
        return offerRepository.findByProductId(productId, active).stream()
                .sorted(Comparator.comparing(Offer::getExpiresAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDto).toList();
    }


    /**
     * Принять/отклонить оффер.
     * При принятии — обновляет статус заявки на APPROVED.
     * При отклонении — заявка остаётся REVIEWING (можно пересчитать/сформировать другой оффер).
     */

    @Override
    public OfferResponse decideOffer(UUID id, OfferDecisionRequest request) {
        Offer offer = offerRepository.findById(id)
                .filter(Offer::isActive).orElseThrow(() -> new ResourceNotFoundException("Offer", id));

        OfferStatus decision = EnumUtils.parseEnumOrThrow(OfferStatus.class, request.status(), "status");
        if (decision != OfferStatus.ACCEPTED && decision != OfferStatus.REJECTED) {
            throw new BusinessLogicException("Only ACCEPTED or REJECTED decisions are allowed");
        }

        // Проверка состояния оффера
        ensureDecidable(offer);


        if (decision == OfferStatus.REJECTED) {
            offer.setStatus(OfferStatus.REJECTED);
            offerRepository.save(offer);
            return toDto(offer);
        }

        // Нет ли принятого оффера
        UUID applicationId = offer.getApplication().getId();
        List<Offer> allActiveOffers = offerRepository.findByApplicationId(applicationId, true);

        boolean hasAccepted = allActiveOffers.stream()
                .anyMatch(o -> !o.getId().equals(offer.getId()) && o.getStatus() == OfferStatus.ACCEPTED);
        if (hasAccepted) {
            throw new BusinessLogicException("Application already has an ACCEPTED offer");
        }


        offer.setStatus(OfferStatus.ACCEPTED);
        offerRepository.save(offer);

        // Обновление заявки
        Application app = offer.getApplication();
        app.setApplicationStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(app);

        allActiveOffers.stream()
                .filter(o -> !o.getId().equals(offer.getId()))
                .filter(o -> o.getStatus() == OfferStatus.PROPOSED)
                .forEach(o -> {
                    o.setStatus(OfferStatus.CANCELED);
                    offerRepository.save(o);
                });
        generatePaymentSchedule(offer, app.getClient());

        return toDto(offer);
    }

    private void ensureDecidable(Offer offer) {
        if (!offer.isActive()) {
            throw new BusinessLogicException("Offer is not active");
        }
        if (offer.getStatus() != OfferStatus.PROPOSED) {
            throw new BusinessLogicException("Offer in status " + offer.getStatus() + " cannot be decided");
        }
        OffsetDateTime exp = offer.getExpiresAt();
        if (exp != null && exp.isBefore(OffsetDateTime.now())) {
            // Синхронизация статуса
            offer.setStatus(OfferStatus.EXPIRED);
            offerRepository.save(offer);
            throw new BusinessLogicException("Offer already expired and cannot be decided");
        }
        if (!offer.getProduct().isActive()) {
            throw new BusinessLogicException("Product of this offer is inactive");
        }
    }

    private void generatePaymentSchedule(Offer offer, Client client) {
        List<Payment> existing = paymentRepository.findByOfferId(offer.getId());
        boolean hasAnyActive = existing.stream().anyMatch(Payment::isActive);
        if (hasAnyActive) {
            throw new BusinessLogicException("Payment schedule already exists for offer " + offer.getId());
        }

        int term = offer.getTermMonths();
        if (term <= 0) {
            throw new BusinessLogicException("Offer termMonths must be > 0");
        }

        BigDecimal monthlyPayment = offer.getMonthlyPayment();
        if (monthlyPayment == null || monthlyPayment.signum() <= 0) {
            throw new BusinessLogicException("Monthly payment must be positive");
        }

        LocalDate start = LocalDate.now().plusMonths(1);

        List<Payment> payments = new ArrayList<>(term);
        for (int i = 0; i < term; i++) {
            LocalDate due = addMonthsSafe(start, i); // Корректный сдвиг месяца с учётом конца месяца
            Payment p = new Payment(offer, client, monthlyPayment, due.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime());

            payments.add(p);
        }

        paymentRepository.saveAll(payments);
    }

    private LocalDate addMonthsSafe(LocalDate date, int months) {
        LocalDate target = date.plusMonths(months);
        int lastDay = target.lengthOfMonth();
        int day = Math.min(date.getDayOfMonth(), lastDay);
        return target.withDayOfMonth(day);
    }

    @Override
    public void cancelAllByApplication(UUID applicationId) {
        List<Offer> offers = offerRepository.findByApplicationId(applicationId, true);
        if (offers.isEmpty()) return;

        OffsetDateTime now = OffsetDateTime.now();
        List<Offer> toUpdate = new ArrayList<>();

        for (Offer o : offers) {
            switch (o.getStatus()) {
                case PROPOSED -> {
                    if (o.getExpiresAt() != null && o.getExpiresAt().isBefore(now)) {
                        o.setStatus(OfferStatus.EXPIRED);
                    } else {
                        o.setStatus(OfferStatus.CANCELED);
                    }
                    toUpdate.add(o);
                }
                case ACCEPTED -> throw new BusinessLogicException(
                            "Cannot cancel offers for application " + applicationId + ": accepted offer exists (" + o.getId() + ")"
                    );

            }
        }

        if (!toUpdate.isEmpty()) {
            offerRepository.saveAll(toUpdate);
        }
    }


    @Override
    public List<OfferResponse> getByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return offerRepository.findByIds(ids, active).stream().map(this::toDto).toList();
    }

    @Override
    public List<OfferResponse> getByApplicationIds(Set<UUID> appIds, boolean active) {
        if (appIds == null || appIds.isEmpty()) return List.of();
        return offerRepository.findByApplicationIds(appIds, active).stream()
                .sorted(Comparator.comparing(Offer::getExpiresAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toDto).toList();
    }

    @Override
    public List<OfferResponse> getByProductIds(Set<UUID> productIds, boolean active) {
        if (productIds == null || productIds.isEmpty()) return List.of();
        return offerRepository.findByProductIds(productIds, active).stream().map(this::toDto).toList();
    }




    private OfferResponse toDto(Offer o) {
        return new OfferResponse(
                o.getId(),
                o.getApplication().getId(),
                o.getProduct().getId(),
                o.getApprovedAmount(),
                o.getTermMonths(),
                o.getApr(),
                o.getMonthlyPayment(),
                o.getExpiresAt(),
                o.getStatus().name(),
                o.isActive()
        );
    }


    @Autowired
    public void setOfferRepository(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setPaymentRepository(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}
