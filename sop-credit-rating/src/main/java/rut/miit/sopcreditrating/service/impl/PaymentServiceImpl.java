package rut.miit.sopcreditrating.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.request.PaymentStatusUpdateRequest;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.Payment;
import rut.miit.sopcreditrating.entity.enums.PaymentStatus;
import rut.miit.sopcreditrating.repository.PaymentRepository;
import rut.miit.sopcreditrating.service.PaymentService;
import rut.miit.sopcreditrating.util.EnumUtils;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;

    @Override
    public PaymentResponse updateStatus(UUID id, PaymentStatusUpdateRequest request) {
        Payment payment = paymentRepository.findById(id)
                .filter(Payment::isActive).orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = EnumUtils.parseEnumOrThrow(PaymentStatus.class, request.status(), "status");

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BusinessLogicException(
                    "Cannot change payment status from " + currentStatus + " to " + newStatus
            );
        }

        if (request.reference() != null && !request.reference().isBlank()) {
            String ref = request.reference().trim();

            if (payment.getReference() != null) {
                throw new BusinessLogicException("Reference for this payment already set and cannot be overwritten");
            }

            paymentRepository.findByReference(ref)
                    .filter(Payment::isActive)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(payment.getId())) {
                            throw new BusinessLogicException("Payment with reference '" + ref + "' already exists");
                        }
                    });

            payment.setReference(ref);
        }

        payment.setStatus(newStatus);
        payment.setProcessedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        return toDto(payment);
    }

    private boolean isValidStatusTransition(PaymentStatus current, PaymentStatus next) {
        return switch (current) {
            case PLANNED -> Set.of(PaymentStatus.COMPLETED, PaymentStatus.FAILED, PaymentStatus.CANCELED, PaymentStatus.DELAYED).contains(next);
            case DELAYED -> Set.of(PaymentStatus.COMPLETED, PaymentStatus.FAILED, PaymentStatus.CANCELED).contains(next);
            case FAILED -> next == PaymentStatus.CANCELED;
            default -> false;
        };
    }

    @Override
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toDto(payment);
    }

    @Override
    public PaymentResponse getByReference(String reference) {
        Payment p = paymentRepository.findByReference(reference).orElseThrow(() -> new ResourceNotFoundException("Payment with reference=" + reference + " not found"));
        return toDto(p);
    }


    @Override
    public List<PaymentResponse> getByOffer(UUID offerId) {
        boolean active = true;
        return getByOffer(offerId, active);
    }

    @Override
    public List<PaymentResponse> getByOffer(UUID offerId, boolean active) {
        List<Payment> payments = paymentRepository.findByOfferId(offerId);

        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for offerId=" + offerId);
        }

        return payments.stream()
                .filter(payment -> payment.isActive() == active)
                .sorted(Comparator.comparing(Payment::getDueDate))
                .map(this::toDto).toList();
    }

    @Override
    public List<PaymentResponse> getByClient(UUID clientId) {
        boolean active = true;
        return getByClient(clientId, active);
    }

    @Override
    public List<PaymentResponse> getByClient(UUID clientId, boolean active) {
        return paymentRepository.findClientPayments(clientId, active).stream()
                .sorted(Comparator.comparing(Payment::getDueDate).reversed())
                .map(this::toDto).toList();
    }

    protected void deleteLogicPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .filter(Payment::isActive).orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        payment.setActive(false);
        paymentRepository.save(payment);
    }

    @Override
    public PagedResponse<PaymentResponse> getAllPayments(int page, int size) {
        boolean active = true;
        return getAllPayments(page, size, active);
    }

    @Override
    public PagedResponse<PaymentResponse> getAllPayments(int page, int size, boolean active) {
        if (page < 0) throw new BusinessLogicException("page must be >= 0");
        if (size <= 0) throw new BusinessLogicException("size must be > 0");

        Sort sort = Sort.by(Sort.Direction.DESC, "dueDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Payment> pageResult = paymentRepository.findAll(pageable, active);

        return new PagedResponse<>(
                pageResult.getContent().stream().map(this::toDto).toList(),
                pageResult.getNumber(),
                pageResult.getSize(),
                (int) pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    @Override
    public List<PaymentResponse> getByClientIds(Set<UUID> clientIds, boolean active) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        return paymentRepository.findByClientIds(clientIds, active).stream()
                .sorted(Comparator.comparing(Payment::getDueDate))
                .map(this::toDto).toList();
    }

    @Override
    public List<PaymentResponse> getByOfferIds(Set<UUID> offerIds, boolean active) {
        if (offerIds == null || offerIds.isEmpty()) return List.of();
        return paymentRepository.findByOfferIds(offerIds, active).stream()
                .sorted(Comparator.comparing(Payment::getDueDate))
                .map(this::toDto).toList();
    }


    private PaymentResponse toDto(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOffer().getId(),
                payment.getClient().getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getReference(),
                payment.getDueDate(),
                payment.getProcessedAt(),
                payment.isActive()
        );
    }

    @Autowired
    public void setPaymentRepository(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}