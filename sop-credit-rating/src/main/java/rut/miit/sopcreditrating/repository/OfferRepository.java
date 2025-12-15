package rut.miit.sopcreditrating.repository;

import rut.miit.sopcreditrating.entity.Offer;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OfferRepository extends
        CreateRepository<Offer, UUID>, ReadRepository<Offer, UUID>, UpdateRepository<Offer, UUID> {
    List<Offer> findAllByClientId(UUID userId);
    List<Offer> findByApplicationId(UUID applicationId, boolean active);

    List<Offer> findByProductId(UUID productId, boolean active);

    List<Offer> findByIds(Set<UUID> ids, boolean active);
    List<Offer> findByApplicationIds(Set<UUID> appIds, boolean active);
    List<Offer> findByProductIds(Set<UUID> productIds, boolean active);

}
