package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.GeoPoint;

@Repository
public interface GeoPointRepository extends PagingAndSortingRepository<GeoPoint, Long> {
    GeoPoint findByLatitudeAndLongitude(double latitude, double longitude);
}
