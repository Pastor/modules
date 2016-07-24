package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.EndPoint;
import ru.phi.modules.entity.GeoPoint;

import java.util.Set;

@Repository("endPointRepository.v1")
public interface EndPointRepository extends PagingAndSortingRepository<EndPoint, Long> {
    Set<EndPoint> findByPoint(GeoPoint point);
}
