package rut.miit.sopcreditrating.repository;

import rut.miit.sopcreditrating.entity.Product;
import rut.miit.sopcreditrating.entity.enums.Purpose;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductRepository extends
        CreateRepository<Product, UUID>, ReadRepository<Product, UUID>, UpdateRepository<Product, UUID> {

    Optional<Product> findByCode(String code);

    List<Product> findAll(boolean active);

    List<Product> findByPurpose(Purpose purpose, boolean active);

    List<Product> findByIds(Set<UUID> ids, boolean active);
}

