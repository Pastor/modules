package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Settings;

@Repository("settingsRepository.v1")
@Service
public interface SettingsRepository extends PagingAndSortingRepository<Settings, Long> {
}
