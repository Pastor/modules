package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Element;

@Repository
public interface ElementRepository extends PagingAndSortingRepository<Element, Long> {
}
