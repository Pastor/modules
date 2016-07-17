package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.entity.Error;
import ru.phi.modules.entity.News;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {
}
