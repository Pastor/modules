package ru.phi.modules.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Profile;

@Repository("newsRepository.v1")
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

    @Query(value = "SELECT COUNT(n) FROM News n WHERE n.publishedAt IS NOT NULL AND n.visible = TRUE AND n.profile = :profile")
    long profileCount(@Param("profile") Profile profile);

    Page<News> findByProfile(Profile profile, Pageable pageable);

    @Query(value = "SELECT n FROM News n WHERE n.publishedAt IS NOT NULL AND n.visible = TRUE")
    Page<News> list(Pageable pageable);

    @Query(value = "SELECT COUNT(n) FROM News n WHERE n.publishedAt IS NOT NULL AND n.visible = TRUE")
    long listCount();
}
