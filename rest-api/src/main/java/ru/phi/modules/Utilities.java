package ru.phi.modules;

import ru.phi.modules.entity.GeoPoint;
import ru.phi.modules.entity.User;
import ru.phi.modules.repository.GeoPointRepository;

public final class Utilities {
    public static GeoPoint point(GeoPointRepository repository, User user, GeoPoint point) {
        if (point == null)
            return null;
        return point(repository, user, point.getLatitude(), point.getLongitude());
    }

    public static GeoPoint point(GeoPointRepository repository, User user, double latitude, double longitude) {
        GeoPoint point = repository.findByLatitudeAndLongitude(latitude, longitude);
        if (point == null) {
            point = new GeoPoint();
            point.setUser(user);
            point.setLatitude(latitude);
            point.setLongitude(longitude);
            point = repository.save(point);
        }
        return point;
    }
}
