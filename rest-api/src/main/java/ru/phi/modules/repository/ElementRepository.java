package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.entity.Element;

@Transactional
@Repository("elementRepository.v1")
public interface ElementRepository extends PagingAndSortingRepository<Element, Long> {
}
