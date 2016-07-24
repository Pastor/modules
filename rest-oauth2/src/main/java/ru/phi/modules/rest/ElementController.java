package ru.phi.modules.rest;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.Utilities;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.AccessibilityProcessRepository;
import ru.phi.modules.repository.ElementRepository;
import ru.phi.modules.repository.EndPointRepository;
import ru.phi.modules.repository.GeoPointRepository;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("elementController.v1")
class ElementController extends AbstractController {

    @Autowired
    private GeoPointRepository geoPointRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    @Autowired
    private ElementRepository elementRepository;

    @Autowired
    private AccessibilityProcessRepository acp;

    @RequestMapping(value = "/elements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Element> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return elementRepository.findAll(pageable).getContent();
    }

    @RequestMapping(value = "/elements/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Element get(@PathVariable("id") Long id)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one;
    }

    @PreAuthorize("#oauth2.hasScope('write:element') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.PUT)
    public void put(@AuthenticationPrincipal UserGetter user, @PathVariable("id") Long id, @RequestBody Element element)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setAddress(element.getAddress());
        one.setFullName(element.getFullName());
        one.setName(element.getName());
        one.setInfo(element.getInfo());
        one.setPoint(point(user.user(),
                element.getPoint().getLatitude(), element.getPoint().getLongitude()));
        elementRepository.save(one);
    }

    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Set<EndPoint> getEndPoints(@PathVariable("id") Long id)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one.getEndPoints();
    }

    @PreAuthorize("#oauth2.hasScope('write:element') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.PUT)
    public void updateEndPoints(@AuthenticationPrincipal UserGetter getter,
                                @PathVariable("id") Long id,
                                @RequestBody(required = false) EndPoint[] endpoints)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        if (endpoints == null)
            throw new ValidationException("Запрос не должен содержать пустое тело");
        final User user = getter.user();
        final Set<EndPoint> points = Sets.newHashSet();
        for (EndPoint endPoint : endpoints) {
            final GeoPoint point = point(user, endPoint.getPoint());
            final Set<EndPoint> byPoint = endPointRepository.findByPoint(point);
            if (byPoint.size() == 0) {
                EndPoint p = new EndPoint();
                p.setUser(user);
                p.setPoint(point);
                p.setType(endPoint.getType());
                p.setAccessibility(listEndPoints(endPoint.getAccessibility()));
                p = endPointRepository.save(p);
                byPoint.add(p);
            }
            /** FIXME: Выбираем пурвую точку. Надо сделать Ключ по point и типу */
            points.add(byPoint.iterator().next());
        }
        if (points.size() == 0) {
            final EndPoint point = new EndPoint();
            point.setUser(user);
            point.setType(EndPointType.both);
            point.setPoint(one.getPoint());
            point.setAccessibility(Sets.newHashSet(Utilities.standard(acp)));
            points.add(endPointRepository.save(point));
        }
        one.setEndPoints(points);
        elementRepository.save(one);
    }

    private Set<AccessibilityProcess> listEndPoints(Set<AccessibilityProcess> accessibility) {
        final Set<AccessibilityProcess> processes = Sets.newHashSet();
        for (AccessibilityProcess process : accessibility) {
            final Accessibility a = process.getAccessibility();
            if (a == null)
                throw new ValidationException("Поле accessibility не может быть пустым");
            final AccessibilityType type = process.getType();
            if (type == null)
                throw new ValidationException("Поле accessibility не может быть пустым");
            final AccessibilityProcess ap = acp.findByAccessibilityAndType(
                    a,
                    type);
            processes.add(ap);
        }
        return processes;
    }

    @PreAuthorize("#oauth2.hasScope('delete:element') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.DELETE)
    public void deleteEndPoints(@AuthenticationPrincipal UserGetter getter, @PathVariable("id") Long id)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        final User user = getter.user();
        final Set<EndPoint> points = Sets.newHashSet();
        final EndPoint point = new EndPoint();
        point.setUser(user);
        point.setType(EndPointType.both);
        point.setPoint(one.getPoint());
        point.setAccessibility(Sets.newHashSet(Utilities.standard(acp)));
        points.add(endPointRepository.save(point));
        one.setEndPoints(points);
        elementRepository.save(one);
    }

    @PreAuthorize("#oauth2.hasScope('delete:element') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        if (!elementRepository.exists(id))
            throw new ObjectNotFoundException(id);
        elementRepository.delete(id);
    }

    @Transactional
    @PreAuthorize("#oauth2.hasScope('write:element') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/elements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Element createElement(@AuthenticationPrincipal UserGetter getter,
                          @RequestBody Element element)
            throws AuthenticationException {
        element.clear();
        element.setUser(getter.user());
        elementRepository.save(element);
        return element;
    }

    @RequestMapping(value = "/elements/count", method = RequestMethod.GET)
    public long count() {
        return elementRepository.count();
    }

}
