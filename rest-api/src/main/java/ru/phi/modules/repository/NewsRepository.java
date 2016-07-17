package ru.phi.modules.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Profile;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {
    Page<News> findByProfile(Profile profile, Pageable pageable);
}
