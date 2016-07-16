package ru.phi.modules.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.entity.Version;

@Repository
@Transactional
public interface VersionRepository extends PagingAndSortingRepository<Version, Long> {

    @Query(value = "SELECT v FROM Version v ORDER BY v.createdAt ASC")
    Version last();
}
