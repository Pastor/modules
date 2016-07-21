package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.UserRole;

import java.util.List;

@Repository
public interface ScopeRepository extends PagingAndSortingRepository<Scope, Long> {
    Scope findByNameAndRole(String name, UserRole role);

    List<Scope> findByRole(UserRole role);
}
