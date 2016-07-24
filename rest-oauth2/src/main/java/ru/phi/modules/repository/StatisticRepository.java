package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Statistic;

@Repository("staticRepository.v1")
public interface StatisticRepository extends PagingAndSortingRepository<Statistic, Long> {
}
