package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.ElementCategory;

@Repository("elementCategoryRepository.v1")
public interface ElementCategoryRepository extends PagingAndSortingRepository<ElementCategory, Long> {
}
