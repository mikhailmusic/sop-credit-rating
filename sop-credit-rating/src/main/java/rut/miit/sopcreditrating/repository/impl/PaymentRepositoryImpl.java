package rut.miit.sopcreditrating.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import rut.miit.sopcreditrating.entity.Payment;
import rut.miit.sopcreditrating.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl extends BaseRepository<Payment, UUID> implements PaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PaymentRepositoryImpl() {
        super(Payment.class);
    }

    @Override
    public List<Payment> findByOfferId(UUID offerId) {
        return entityManager.createQuery("SELECT p FROM Payment p WHERE p.offer.id = :offerId", Payment.class)
                .setParameter("offerId", offerId)
                .getResultList();
    }

    @Override
    public Optional<Payment> findByReference(String reference) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT p FROM Payment p WHERE LOWER(p.reference) = LOWER(:reference)", Payment.class)
                            .setParameter("reference", reference)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Payment> findClientPayments(UUID clientId, boolean active) {
        return entityManager.createQuery("SELECT p FROM Payment p WHERE p.client.id = :clientId AND p.active = :active", Payment.class)
                .setParameter("clientId", clientId)
                .setParameter("active", active)
                .getResultList();
    }


    @Override
    public List<Payment> findByClientIds(Set<UUID> clientIds, boolean active) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        return entityManager.createQuery("SELECT p FROM Payment p WHERE p.client.id IN :clientIds AND p.active = :active", Payment.class)
                .setParameter("clientIds", clientIds)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Payment> findByOfferIds(Set<UUID> offerIds, boolean active) {
        if (offerIds == null || offerIds.isEmpty()) return List.of();
        return entityManager.createQuery("SELECT p FROM Payment p WHERE p.offer.id IN :offerIds AND p.active = :active", Payment.class)
                .setParameter("offerIds", offerIds)
                .setParameter("active", active)
                .getResultList();
    }
}

