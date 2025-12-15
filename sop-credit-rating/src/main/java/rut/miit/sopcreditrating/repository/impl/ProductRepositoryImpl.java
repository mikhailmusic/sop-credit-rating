package rut.miit.sopcreditrating.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import rut.miit.sopcreditrating.entity.Product;
import rut.miit.sopcreditrating.entity.enums.Purpose;
import rut.miit.sopcreditrating.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl extends BaseRepository<Product, UUID> implements ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public ProductRepositoryImpl() { super(Product.class); }

    @Override
    public Optional<Product> findByCode(String code) {
        try {
            return Optional.of(
                    em.createQuery("SELECT p FROM Product p WHERE p.code = :code ", Product.class)
                            .setParameter("code", code).getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll(boolean active) {
        return em.createQuery("SELECT p FROM Product p WHERE p.active = :active", Product.class)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Product> findByPurpose(Purpose purpose, boolean active) {
        return em.createQuery("SELECT p FROM Product p WHERE p.purpose = :purpose AND p.active = :active", Product.class)
                .setParameter("purpose", purpose)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public List<Product> findByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return em.createQuery("SELECT p FROM Product p WHERE p.id IN :ids AND p.active = :active", Product.class)
                .setParameter("ids", ids)
                .setParameter("active", active)
                .getResultList();
    }
}

