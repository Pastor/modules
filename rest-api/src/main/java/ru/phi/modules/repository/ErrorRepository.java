package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.entity.Error;

@Transactional
public interface ErrorRepository extends PagingAndSortingRepository<Error, Long> {
}
