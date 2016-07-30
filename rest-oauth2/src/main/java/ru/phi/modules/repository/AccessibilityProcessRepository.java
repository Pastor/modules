package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.AccessibilityProcess;
import ru.phi.modules.entity.AccessibilityType;

@Repository("accessibilityProcessRepository.v1")
@Service
public interface AccessibilityProcessRepository extends PagingAndSortingRepository<AccessibilityProcess, Long> {
    AccessibilityProcess findByAccessibilityAndType(Accessibility accessibility, AccessibilityType type);
}
