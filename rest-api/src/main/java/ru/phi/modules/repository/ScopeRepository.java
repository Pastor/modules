package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.phi.modules.entity.Scope;

public interface ScopeRepository extends PagingAndSortingRepository<Scope, Long> {
}
