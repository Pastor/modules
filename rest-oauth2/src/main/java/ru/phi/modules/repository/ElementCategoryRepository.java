package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.ElementCategory;

@Repository("elementCategoryRepository.v1")
@Service
public interface ElementCategoryRepository extends PagingAndSortingRepository<ElementCategory, Long> {
}
