package rut.miit.sopcreditrating.repository.generic;

import org.springframework.data.repository.NoRepositoryBean;
import rut.miit.sopcreditrating.entity.BaseEntity;

@NoRepositoryBean
public interface UpdateRepository<T extends BaseEntity, ID> {
    T update(T entity);
}
