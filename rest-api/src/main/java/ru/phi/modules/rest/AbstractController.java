package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import ru.phi.modules.entity.GeoPoint;
import ru.phi.modules.entity.User;
import ru.phi.modules.repository.GeoPointRepository;

abstract class AbstractController {

    @Autowired
    protected GeoPointRepository geoPointRepository;

    protected GeoPoint point(User user, GeoPoint point) {
        if (point == null)
            return null;
        return point(user, point.getLatitude(), point.getLongitude());
    }

    protected GeoPoint point(User user, double latitude, double longitude) {
        return ru.phi.modules.Utilities.point(geoPointRepository, user, latitude, longitude);
    }
}
