package ru.phi.modules.rest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.Utilities;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ElementControllerTest extends AbstractRestTest {

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsGet() throws Exception {
        final Token token = newToken("element");
        environment.getElement(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsDelete() throws Exception {
        final Token token = newToken("element");
        environment.deleteElement(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsDeleteEndPoint() throws Exception {
        final Token token = newToken("element");
        environment.deleteEndpoints(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsUpdate() throws Exception {
        final Token token = newToken("element");
        final Element element = new Element();
        element.setName("000000000000000000");
        element.setFullName("000000000000000000");
        element.setAddress("ADDRESS");
        element.setPoint(point(successUser, 0, 0));
        environment.update(token.getKey(), 11111L, element);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsGetEndpoints() throws Exception {
        final Token token = newToken("element");
        environment.endpoints(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsUpdateEndpoints() throws Exception {
        final Token token = newToken("element");
        environment.update(token.getKey(), 11111L, new EndPoint[0]);
    }

    @Test(expected = ValidationException.class)
    public void faultValidateUpdateEndpoints() throws Exception {
        final Token token = newToken("element");
        final Element element = createElement(successUser,
                "NAME", "FULL_NAME", "ADDRESS", 54.0000, 55.00000, hospital);
        environment.update(token.getKey(), element.getId(), (EndPoint[]) null);
    }

    @Test(expected = ValidationException.class)
    public void faultValidateUpdateNewEndPointsWithoutAccessibilityProperty() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        final EndPoint endPoint = new EndPoint();
        final GeoPoint point = new GeoPoint();
        point.setLongitude(0);
        point.setLatitude(0);
        endPoint.setPoint(point);
        endPoint.setType(EndPointType.both);
        final AccessibilityProcess standard = Utilities.standard(accessibilityProcessRepository);
        standard.setAccessibility(null);
        endPoint.setAccessibility(Sets.newHashSet(standard));
        environment.update(token.getKey(), element.getId(), new EndPoint[]{endPoint});
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 1);
    }

    @Test(expected = ValidationException.class)
    public void faultValidateUpdateNewEndPointsWithoutTypeProperty() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        final EndPoint endPoint = new EndPoint();
        final GeoPoint point = new GeoPoint();
        point.setLongitude(0);
        point.setLatitude(0);
        endPoint.setPoint(point);
        endPoint.setType(EndPointType.both);
        final AccessibilityProcess standard = Utilities.standard(accessibilityProcessRepository);
        standard.setType(null);
        endPoint.setAccessibility(Sets.newHashSet(standard));
        environment.update(token.getKey(), element.getId(), new EndPoint[]{endPoint});
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 1);
    }

    @Test
    public void list() throws Exception {
        final Token token = newToken();
        final int size = 6;
        createElements(size);
        final List<Element> elements = environment.elements(token.getKey());
        assertEquals(elements.size(), size);
    }

    @Test
    public void get() throws Exception {
        final Token token = newToken();
        final Element element = createElement(successUser, "NAME", "FULL_NAME", "ADDRESS", 54.0000, 55.00000, hospital, standardAccPro());
        final Element element2 = environment.getElement(token.getKey(), element.getId());
        assertNotNull(element2);
        assertEquals(element.getName(), element2.getName());
    }

    @Test
    public void update() throws Exception {
        final Token token = newToken("element");
        final Element element = createElement(successUser, "NAME", "FULL_NAME", "ADDRESS", 54.0000, 55.00000, hospital, standardAccPro());
        element.setName("NEW_NAME");
        environment.update(token.getKey(), element.getId(), element);
        final Element element2 = elementRepository.findOne(element.getId());
        assertNotNull(element2);
        assertNotSame(element.getName(), element2.getName());
        assertEquals("NEW_NAME", element2.getName());
    }

    @Test
    public void getEndPoints() throws Exception {
        final Token token = newToken();
        final Element element = createElementWithEndPoints();
        final List<EndPoint> endPoints = environment.endpoints(token.getKey(), element.getId());
        assertEquals(endPoints.size(), 5);
    }

    protected final Element createElementWithEndPoints() {
        Element element = createElement(successUser, "NAME", "FULL_NAME", "ADDRESS", 54.0090, 55.90000, hospital);
        final Set<EndPoint> endpoints = Sets.newHashSet(
                createEndpoint(successUser, 34.00000, 54.00000, EndPointType.enter, standardAccPro()),
                createEndpoint(successUser, 34.10000, 54.00000, EndPointType.enter, standardAccPro()),
                createEndpoint(successUser, 34.20000, 54.00000, EndPointType.enter, standardAccPro()),
                createEndpoint(successUser, 34.30000, 54.00000, EndPointType.enter, standardAccPro()),
                createEndpoint(successUser, 34.40000, 54.00000, EndPointType.enter, standardAccPro())
        );
        element.setEndPoints(endpoints);
        element = elementRepository.save(element);
        return element;
    }

    @Test
    public void updateEndPoints() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        final Set<EndPoint> endpoints = Sets.newHashSet(
                createEndpoint(successUser, 34.00000, 54.00000, EndPointType.enter, standardAccPro()),
                createEndpoint(successUser, 34.40000, 54.04000, EndPointType.enter, standardAccPro())
        );
        environment.update(token.getKey(), element.getId(), endpoints.toArray(new EndPoint[endpoints.size()]));
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 2);
    }

    @Test
    public void updateEmptyEndPoints() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        environment.update(token.getKey(), element.getId(), new EndPoint[0]);
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 1);
    }

    @Test
    public void updateNewEndPoints() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        final EndPoint endPoint = new EndPoint();
        final GeoPoint point = new GeoPoint();
        point.setLongitude(0);
        point.setLatitude(0);
        endPoint.setPoint(point);
        endPoint.setType(EndPointType.both);
        endPoint.setAccessibility(Sets.newHashSet(Utilities.standard(accessibilityProcessRepository)));
        environment.update(token.getKey(), element.getId(), new EndPoint[]{endPoint});
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 1);
    }

    @Test
    public void deleteEndPoints() throws Exception {
        final Token token = newToken("element");
        final Element element = createElementWithEndPoints();
        environment.deleteEndpoints(token.getKey(), element.getId());
        final Element one = environment.getElement(token.getKey(), element.getId());
        assertEquals(one.getEndPoints().size(), 1);
        assertEquals(one.getEndPoints().iterator().next().getPoint(), one.getPoint());
    }

    @Test
    public void delete() throws Exception {
        final Token token = newToken("element");
        final int size = 6;
        createElements(size);
        final Element element = elementRepository.findAll().iterator().next();
        environment.deleteElement(token.getKey(), element.getId());
        assertEquals(size - 1, elementRepository.count());
    }

    @Test
    public void createElement() throws Exception {
        final Token token = newToken("element");
        final int size = 6;
        createElements(size);
        final List<Element> all = Lists.newArrayList(elementRepository.findAll());
        final Element element = all.get(0);
        element.setName("NEW000000000000000");
        element.clear();
        element.setPolygon(null);
        element.setEndPoints(null);
        element.setAccessibilityProcesses(null);
        element.setCategories(Sets.newHashSet(hospital));
        element.setAccessibilityProcesses(Sets.newHashSet(standardAccPro()));
        final Element element2 = environment.createElement(token.getKey(), element);
        assertNotNull(element2);
        assertEquals(element2.getName(), "NEW000000000000000");
        assertEquals(size + 1, elementRepository.count());
    }

    @Test
    public void count() throws Exception {
        final Token token = newToken("element");
        final int size = 6;
        createElements(size);
        assertEquals(size, environment.elementsCount(token.getKey()).longValue());
    }

    private void createElements(int size) {
        for (int i = 0; i < size; ++i) {
            createElement(successUser, "NAME" + i, "FULL_NAME" + i, "ADDRESS" + i, 50.0000 + i, 55.00000 + i, hospital, standardAccPro());
        }
    }
}