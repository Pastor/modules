package ru.phi.modules;

import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.SystemException;
import ru.phi.modules.repository.AccessibilityProcessRepository;
import ru.phi.modules.repository.GeoPointRepository;

public final class Utilities {
    public static GeoPoint point(GeoPointRepository repository, User user, GeoPoint point) {
        if (point == null)
            return null;
        if (repository == null)
            throw new SystemException("Репозиторий не может быть равен нулю");
        if (user == null)
            throw new SystemException("Пользователь не может быть пустым");
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

    public static void register(AccessibilityProcessRepository acp) {
        for (Accessibility accessibility : Accessibility.values()) {
            for (AccessibilityType type : AccessibilityType.values()) {
                final AccessibilityProcess entity = new AccessibilityProcess();
                entity.setAccessibility(accessibility);
                entity.setType(type);
                acp.save(entity);
            }
        }
    }

    public static AccessibilityProcess standard(AccessibilityProcessRepository acp) {
        return acp.findByAccessibilityAndType(Accessibility.normal, AccessibilityType.full);
    }
}
