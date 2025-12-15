package rut.miit.sopcreditrating.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import rut.miit.sopcreditrating.entity.Client;
import rut.miit.sopcreditrating.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class ClientRepositoryImpl extends BaseRepository<Client, UUID> implements ClientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ClientRepositoryImpl() {
        super(Client.class);
    }

    @Override
    public Optional<Client> findByCif(String cif) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT c FROM Client c WHERE c.cif = :cif", Client.class)
                            .setParameter("cif", cif)
                            .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT c FROM Client c WHERE c.email = :email", Client.class)
                            .setParameter("email", email)
                            .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Client> findByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return entityManager.createQuery(
                        "SELECT c FROM Client c WHERE c.id IN :ids AND c.active = :active", Client.class)
                .setParameter("ids", ids)
                .setParameter("active", active)
                .getResultList();
    }

}