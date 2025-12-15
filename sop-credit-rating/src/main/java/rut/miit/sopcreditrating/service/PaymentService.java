package rut.miit.sopcreditrating.service;

import rut.miit.sopcontracts.dto.request.PaymentStatusUpdateRequest;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.dto.response.PaymentResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse getPayment(UUID id);

    PaymentResponse getByReference(String reference);

    List<PaymentResponse> getByOffer(UUID offerId);
    List<PaymentResponse> getByOffer(UUID offerId, boolean active);

    List<PaymentResponse> getByClient(UUID clientId);
    List<PaymentResponse> getByClient(UUID clientId, boolean active);

    List<PaymentResponse> getByClientIds(Set<UUID> clientIds, boolean active);
    List<PaymentResponse> getByOfferIds(Set<UUID> offerIds, boolean active);

    PaymentResponse updateStatus(UUID id, PaymentStatusUpdateRequest request);

    PagedResponse<PaymentResponse> getAllPayments(int page, int size);
    PagedResponse<PaymentResponse> getAllPayments(int page, int size, boolean active);
}
