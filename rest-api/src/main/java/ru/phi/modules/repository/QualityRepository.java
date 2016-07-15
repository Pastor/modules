package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.Quality;

@Repository
public interface QualityRepository extends PagingAndSortingRepository<Quality, Long> {
}
