package rut.miit.sopcreditrating.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rut.miit.sopcreditrating.entity.Application;
import rut.miit.sopcreditrating.entity.enums.ApplicationStatus;
import rut.miit.sopcreditrating.repository.generic.CreateRepository;
import rut.miit.sopcreditrating.repository.generic.ReadRepository;
import rut.miit.sopcreditrating.repository.generic.UpdateRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicationRepository extends
        CreateRepository<Application, UUID>, ReadRepository<Application, UUID>,UpdateRepository<Application, UUID> {

    List<Application> findAllByClientId(UUID uuid);
    Page<Application> findApplications(Pageable pageable, ApplicationStatus status, boolean active);

    List<Application> findByIds(Set<UUID> ids, boolean active);
    List<Application> findByClientIds(Set<UUID> clientIds, boolean active);

}
