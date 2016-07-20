package ru.phi.modules.rest;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;
import ru.phi.modules.repository.ElementRepository;
import ru.phi.modules.repository.EndPointRepository;
import ru.phi.modules.repository.GeoPointRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
class ElementController extends AbstractController {

    @Autowired
    private GeoPointRepository geoPointRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    @Autowired
    private ElementRepository elementRepository;

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

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.PUT)
    public void put(@AuthorizedToken Token token, @PathVariable("id") Long id, @RequestBody Element element)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setAddress(element.getAddress());
        one.setFullName(element.getFullName());
        one.setName(element.getName());
        one.setInfo(element.getInfo());
        one.setPoint(point(token.getUser(),
                element.getPoint().getLatitude(), element.getPoint().getLongitude()));
        elementRepository.save(one);
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.GET)
    public Set<EndPoint> getEndPoints(@PathVariable("id") Long id)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one.getEndPoints();
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.PUT)
    public void putEndPoints(@AuthorizedToken Token token,
                             @PathVariable("id") Long id,
                             @RequestBody(required = false) EndPoint[] endpoints)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        if (endpoints == null)
            throw new ValidationException("Запрос не должен содержать пустое тело");
        final User user = token.getUser();
        final Set<EndPoint> points = Sets.newHashSet();
        for (EndPoint endPoint : endpoints) {
            final GeoPoint point = point(user, endPoint.getPoint());
            final Set<EndPoint> byPoint = endPointRepository.findByPoint(point);
            if (byPoint.size() == 0) {
                EndPoint p = new EndPoint();
                p.setUser(user);
                p.setPoint(point);
                p.setType(endPoint.getType());
                p = endPointRepository.save(p);
                byPoint.add(p);
            }
            /** FIXME: Выбираем пурвую точку. Надо сделать Ключ по point и типу */
            points.add(byPoint.iterator().next());
        }
        if (points.size() == 0) {
            final EndPoint point = new EndPoint();
            point.setUser(user);
            point.setType(EndPointType.BOTH);
            point.setPoint(one.getPoint());
            points.add(endPointRepository.save(point));
        }
        one.setEndPoints(points);
        elementRepository.save(one);
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}/endpoints", method = RequestMethod.DELETE)
    public void deleteEndPoints(@AuthorizedToken Token token, @PathVariable("id") Long id)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        final User user = token.getUser();
        final Set<EndPoint> points = Sets.newHashSet();
        final EndPoint point = new EndPoint();
        point.setUser(user);
        point.setType(EndPointType.BOTH);
        point.setPoint(one.getPoint());
        points.add(endPointRepository.save(point));
        one.setEndPoints(points);
        elementRepository.save(one);
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        if (!elementRepository.exists(id))
            throw new ObjectNotFoundException(id);
        elementRepository.delete(id);
    }

    @Transactional
    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Element createElement(@AuthorizedToken Token token,
                          @RequestBody Element element)
            throws AuthenticationException {
        element.clear();
        element.setUser(token.getUser());
        elementRepository.save(element);
        return element;
    }

    @RequestMapping(value = "/elements/count", method = RequestMethod.GET)
    public long count() {
        return elementRepository.count();
    }

}
