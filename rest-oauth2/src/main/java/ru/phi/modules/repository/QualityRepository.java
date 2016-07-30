package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Quality;

@Repository("qualityRepository.v1")
@Service
public interface QualityRepository extends PagingAndSortingRepository<Quality, Long> {
}
