package rut.miit.sopcreditrating.repository;

import rut.miit.sopcreditrating.entity.Client;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ClientRepository extends
        CreateRepository<Client, UUID>, ReadRepository<Client, UUID>, UpdateRepository<Client, UUID> {

    Optional<Client> findByCif(String cif);
    Optional<Client> findByEmail(String email);

    List<Client> findByIds(Set<UUID> ids, boolean active);
}
