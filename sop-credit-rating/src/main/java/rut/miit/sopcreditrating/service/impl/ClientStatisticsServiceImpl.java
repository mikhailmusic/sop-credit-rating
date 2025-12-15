package rut.miit.sopcreditrating.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.*;
import rut.miit.sopcreditrating.entity.enums.*;
import rut.miit.sopcreditrating.repository.*;
import rut.miit.sopcreditrating.service.ClientStatisticsService;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ClientStatisticsServiceImpl implements ClientStatisticsService {
    private ClientRepository clientRepository;
    private ApplicationRepository applicationRepository;
    private PaymentRepository paymentRepository;
    private OfferRepository offerRepository;


    @Override
    public ClientStatisticsResponse calculateStatistics(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .filter(Client::isActive).orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime twelveMonthsAgo = now.minusMonths(12);

        List<Application> allApplications = applicationRepository.findAllByClientId(clientId)
                .stream().filter(Application::isActive).toList();

        int approved = (int) allApplications.stream().filter(a -> a.getApplicationStatus() == ApplicationStatus.APPROVED).count();
        int rejected = (int) allApplications.stream().filter(a -> a.getApplicationStatus() == ApplicationStatus.REJECTED).count();
        int pending = (int) allApplications.stream().filter(a -> a.getApplicationStatus() == ApplicationStatus.REVIEWING).count();

        int total = allApplications.size();

        OffsetDateTime lastAppDate = allApplications.stream()
                .map(Application::getCreatedDate).filter(Objects::nonNull).max(OffsetDateTime::compareTo).orElse(null);

        List<Payment> recentPayments = paymentRepository.findClientPayments(clientId, true)
                .stream()
                .filter(p -> {
                    OffsetDateTime due = p.getDueDate();
                    OffsetDateTime processed = p.getProcessedAt();
                    return (due != null && due.isAfter(twelveMonthsAgo))
                            || (processed != null && processed.isAfter(twelveMonthsAgo));
                })
                .toList();

        int totalPayments12m = recentPayments.size();
        int delayedCount = calculateDelayedOrFailedCount(recentPayments);
        int onTimeCount = totalPayments12m - delayedCount;

        int maxDaysOverdue = recentPayments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.CANCELED)
                .mapToInt(this::calculateDaysPastDue)
                .max()
                .orElse(0);

        List<Offer> activeOffers = offerRepository.findAllByClientId(clientId)
                .stream()
                .filter(Offer::isActive)
                .filter(o -> o.getStatus() == OfferStatus.PROPOSED
                        || o.getStatus() == OfferStatus.ACCEPTED)
                .toList();

        int activeOffersCount = activeOffers.size();

        BigDecimal totalDebt = activeOffers.stream()
                .map(this::calculateRemainingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int age = (int) ChronoUnit.YEARS.between(client.getBirthDate(), LocalDate.now());

        return new ClientStatisticsResponse(
                clientId, approved, rejected, total, pending, client.getAnnualIncome(), client.getTotalMonthlyDebtPayment(),
                client.getEmploymentStatus().name(), delayedCount, maxDaysOverdue, totalPayments12m, onTimeCount,
                activeOffersCount, totalDebt, age, lastAppDate, now
        );
    }

    private int calculateDelayedOrFailedCount(List<Payment> payments) {
        int count = 0;
        OffsetDateTime now = OffsetDateTime.now();
        for (Payment p : payments) {
            boolean isDelayed = switch (p.getStatus()) {
                case FAILED, DELAYED -> true;
                case COMPLETED -> {
                    OffsetDateTime due = p.getDueDate();
                    OffsetDateTime paid = p.getProcessedAt();
                    yield (due != null && paid != null && paid.isAfter(due));
                }
                case PLANNED -> {
                    OffsetDateTime due = p.getDueDate();
                    yield (due != null && now.isAfter(due));
                }
                case CANCELED -> false;
            };
            if (isDelayed) count++;
        }
        return count;
    }

    private int calculateDaysPastDue(Payment p) {
        if (p.getDueDate() == null) return 0;

        LocalDate dueDate = p.getDueDate().toLocalDate();
        LocalDate referenceDate = (p.getProcessedAt() != null)
                ? p.getProcessedAt().toLocalDate()
                : LocalDate.now();

        long days = ChronoUnit.DAYS.between(dueDate, referenceDate);
        return (int) Math.max(days, 0);
    }


    private BigDecimal calculateRemainingBalance(Offer offer) {
        if (offer.getStatus() != OfferStatus.ACCEPTED) {
            return BigDecimal.ZERO;
        }

        BigDecimal approvedAmount = offer.getApprovedAmount();

        BigDecimal paidAmount = offer.getPayments()
                .stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED).map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = approvedAmount.subtract(paidAmount);
        return remaining.max(BigDecimal.ZERO);
    }

    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) { this.applicationRepository = applicationRepository; }

    @Autowired
    public void setPaymentRepository(PaymentRepository paymentRepository) { this.paymentRepository = paymentRepository; }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) { this.clientRepository = clientRepository; }

    @Autowired
    public void setOfferRepository(OfferRepository offerRepository) { this.offerRepository = offerRepository; }
}
