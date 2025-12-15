package rut.miit.sopcreditrating.repository;

import rut.miit.sopcreditrating.entity.Payment;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PaymentRepository extends
        CreateRepository<Payment, UUID>, ReadRepository<Payment, UUID>, UpdateRepository<Payment, UUID> {

    List<Payment> findByOfferId(UUID offerId);

    Optional<Payment> findByReference(String reference);

    List<Payment> findClientPayments(UUID clientId, boolean active);

    List<Payment> findByClientIds(Set<UUID> clientIds, boolean active);
    List<Payment> findByOfferIds(Set<UUID> offerIds, boolean active);
}
