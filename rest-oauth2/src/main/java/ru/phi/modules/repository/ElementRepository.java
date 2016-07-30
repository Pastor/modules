package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Element;

//@Transactional
@Repository("elementRepository.v1")
@Service
public interface ElementRepository extends PagingAndSortingRepository<Element, Long> {
}
