package ru.phi.modules;

import lombok.extern.slf4j.Slf4j;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.SystemException;
import ru.phi.modules.repository.AccessibilityProcessRepository;
import ru.phi.modules.repository.GeoPointRepository;

import javax.persistence.Column;
import java.lang.reflect.Field;

@Slf4j
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
        if (Accessibility.values().length * AccessibilityType.values().length != acp.count()) {
            for (Accessibility accessibility : Accessibility.values()) {
                for (AccessibilityType type : AccessibilityType.values()) {
                    final AccessibilityProcess find = acp.findByAccessibilityAndType(accessibility, type);
                    if (find == null) {
                        final AccessibilityProcess entity = new AccessibilityProcess();
                        entity.setAccessibility(accessibility);
                        entity.setType(type);
                        acp.save(entity);
                    }
                }
            }
        }
    }

    public static AccessibilityProcess standard(AccessibilityProcessRepository acp) {
        return acp.findByAccessibilityAndType(Accessibility.normal, AccessibilityType.full);
    }

    public static int entityColumnLength(Class entityClass, String fieldName) {
        try {
            final Field field = entityClass.getDeclaredField(fieldName);
            final Column[] columns = field.getAnnotationsByType(Column.class);
            return columns[0].length() - 1;
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
}
