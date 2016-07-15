package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Settings;

@Repository
public interface SettingsRepository extends PagingAndSortingRepository<Settings, Long> {
}
