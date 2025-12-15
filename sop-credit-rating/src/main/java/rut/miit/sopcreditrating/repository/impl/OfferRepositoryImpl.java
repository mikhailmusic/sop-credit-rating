package rut.miit.sopcreditrating.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import rut.miit.sopcreditrating.entity.Offer;
import rut.miit.sopcreditrating.repository.OfferRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class OfferRepositoryImpl extends BaseRepository<Offer, UUID> implements OfferRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public OfferRepositoryImpl() {
        super(Offer.class);
    }

    @Override
    public List<Offer> findByApplicationId(UUID applicationId, boolean active) {
        return entityManager.createQuery("SELECT o FROM Offer o WHERE o.application.id = :applicationId AND o.active = :active", Offer.class)
                .setParameter("applicationId", applicationId)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Offer> findByProductId(UUID productId, boolean active) {
        return entityManager.createQuery("SELECT o FROM Offer o WHERE o.product.id = :productId AND o.active = :active", Offer.class)
                .setParameter("productId", productId)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Offer> findByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return entityManager.createQuery("SELECT o FROM Offer o WHERE o.id IN :ids AND o.active = :active", Offer.class)
                .setParameter("ids", ids)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Offer> findByApplicationIds(Set<UUID> appIds, boolean active) {
        if (appIds == null || appIds.isEmpty()) return List.of();
        return entityManager.createQuery("SELECT o FROM Offer o WHERE o.application.id IN :appIds AND o.active = :active", Offer.class)
                .setParameter("appIds", appIds)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Offer> findByProductIds(Set<UUID> productIds, boolean active) {
        if (productIds == null || productIds.isEmpty()) return List.of();
        return entityManager.createQuery("SELECT o FROM Offer o WHERE o.product.id IN :productIds AND o.active = :active", Offer.class)
                .setParameter("productIds", productIds)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Offer> findAllByClientId(UUID clientId) {
        return entityManager.createQuery("SELECT o FROM Offer o JOIN o.application a JOIN a.client c WHERE c.id = :clientId", Offer.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }


}

