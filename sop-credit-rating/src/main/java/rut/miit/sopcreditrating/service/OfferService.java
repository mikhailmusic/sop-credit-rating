package rut.miit.sopcreditrating.service;

import rut.miit.sopcontracts.dto.request.OfferDecisionRequest;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OfferService {

    OfferResponse createOffer(OfferGeneratedEvent request);

    OfferResponse getOffer(UUID id);

    OfferResponse getCurrentByApplication(UUID applicationId);

    List<OfferResponse> getByApplication(UUID applicationId);
    List<OfferResponse> getByApplication(UUID applicationId, boolean active);

    PagedResponse<OfferResponse> getAllOffers(int page, int size);
    PagedResponse<OfferResponse> getAllOffers(int page, int size, boolean active);

    List<OfferResponse> getByProduct(UUID productId);
    List<OfferResponse> getByProduct(UUID productId, boolean active);

    List<OfferResponse> getByIds(Set<UUID> ids, boolean active);
    List<OfferResponse> getByApplicationIds(Set<UUID> appIds, boolean active);
    List<OfferResponse> getByProductIds(Set<UUID> productIds, boolean active);

    OfferResponse decideOffer(UUID id, OfferDecisionRequest request);

    void cancelAllByApplication(UUID applicationId);
}

