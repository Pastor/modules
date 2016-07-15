package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Profile;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long> {
}
