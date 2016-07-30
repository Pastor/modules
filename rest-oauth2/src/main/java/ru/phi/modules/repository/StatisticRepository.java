package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Statistic;

@Repository("staticRepository.v1")
@Service
public interface StatisticRepository extends PagingAndSortingRepository<Statistic, Long> {
}
