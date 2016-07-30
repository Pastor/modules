package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Profile;

@Repository("profileRepository.v1")
@Service
public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long> {
}
