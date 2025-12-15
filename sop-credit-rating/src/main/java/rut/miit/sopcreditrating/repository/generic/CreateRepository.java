package rut.miit.sopcreditrating.repository.generic;

import org.springframework.data.repository.NoRepositoryBean;
import rut.miit.sopcreditrating.entity.BaseEntity;

@NoRepositoryBean
public interface CreateRepository<T extends BaseEntity, ID> {
    T save(T entity);
    Iterable<T> saveAll(Iterable<T> entities);
}
