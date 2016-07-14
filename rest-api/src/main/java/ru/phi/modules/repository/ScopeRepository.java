package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Scope;

@Repository
public interface ScopeRepository extends PagingAndSortingRepository<Scope, Long> {
}
