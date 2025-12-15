package rut.miit.sopcreditrating.repository.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import rut.miit.sopcreditrating.entity.Application;
import rut.miit.sopcreditrating.entity.enums.ApplicationStatus;
import rut.miit.sopcreditrating.repository.ApplicationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class ApplicationRepositoryImpl extends BaseRepository<Application, UUID> implements ApplicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ApplicationRepositoryImpl() {
        super(Application.class);
    }

    @Override
    public List<Application> findAllByClientId(UUID uuid) {

        return entityManager.createQuery("SELECT a FROM Application a WHERE a.client.id = :clientId", Application.class)
                .setParameter("clientId", uuid)
                .getResultList();
    }

    @Override
    public Page<Application> findApplications(Pageable pageable, ApplicationStatus status, boolean active) {

        Specification<Application> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("active"), active));
            if (status != null) {
                predicates.add(cb.equal(root.get("applicationStatus"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return findPage(pageable, spec);
    }


    @Override
    public List<Application> findByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return entityManager.createQuery(
                        "SELECT a FROM Application a WHERE a.id IN :ids AND a.active = :active", Application.class)
                .setParameter("ids", ids)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Application> findByClientIds(Set<UUID> clientIds, boolean active) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        return entityManager.createQuery(
                        "SELECT a FROM Application a WHERE a.client.id IN :clientIds AND a.active = :active", Application.class)
                .setParameter("clientIds", clientIds)
                .setParameter("active", active)
                .getResultList();
    }
}
