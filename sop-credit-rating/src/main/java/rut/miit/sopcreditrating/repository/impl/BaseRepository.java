package rut.miit.sopcreditrating.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.transaction.annotation.Transactional;
import rut.miit.sopcreditrating.entity.BaseEntity;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;


import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T extends BaseEntity, ID> implements
        CreateRepository<T, ID>, ReadRepository<T, ID>, UpdateRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;
    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        entityManager.merge(entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery("FROM " + entityClass.getName(), entityClass)
                .getResultList();
    }

    @Override
    @Transactional
    public Iterable<T> saveAll(Iterable<T> entities) {
        for (T entity : entities) {
            entityManager.persist(entity);
        }
        return entities;
    }


    @Override
    public Page<T> findAll(Pageable pageable, boolean active) {
        Specification<T> spec = (root, query, cb) ->
                cb.equal(root.get("active"), active);

        return findPage(pageable, spec);
    }

    protected Page<T> findPage(Pageable pageable, Specification<T> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        Predicate predicate = (spec != null)
                ? spec.toPredicate(root, cq, cb)
                : cb.conjunction();

        cq.where(predicate);

        if (pageable.getSort().isSorted()) {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        }

        TypedQuery<T> contentQuery = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<T> content = contentQuery.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<T> countRoot = countCq.from(entityClass);
        Predicate countPredicate = (spec != null)
                ? spec.toPredicate(countRoot, countCq, cb)
                : cb.conjunction();

        countCq.select(cb.count(countRoot)).where(countPredicate);

        long total = entityManager.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
