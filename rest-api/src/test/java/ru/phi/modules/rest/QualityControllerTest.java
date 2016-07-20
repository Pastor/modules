package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.Quality;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.ObjectNotFoundException;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class QualityControllerTest extends AbstractRestTest {

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsDelete() throws Exception {
        final Token token = newToken("quality");
        environment.deleteQuality(token.getKey(), 1000L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsGet() throws Exception {
        final Token token = newToken("quality");
        environment.getQuality(token.getKey(), 1000L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsUpdate() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        final Token token = newToken("quality");
        environment.update(token.getKey(), 1000L, a);
    }

    @Test
    public void list() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        createQuality("C", "template-c", Accessibility.eyeless);
        createQuality("D", "template-d", Accessibility.normal);
        final Token token = newToken("quality");
        final List<Quality> qualities = environment.qualities(token.getKey());
        assertEquals(qualities.size(), 4L);
    }

    @Test
    public void get() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        final Quality b = createQuality("B", "template-b", Accessibility.brainless);
        final Token token = newToken("quality");
        final Quality quality = environment.getQuality(token.getKey(), b.getId());
        assertNotNull(quality);
        assertEquals(quality.getName(), b.getName());
    }

    @Test
    public void update() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        final Token token = newToken("quality");
        final Quality quality = environment.getQuality(token.getKey(), a.getId());
        assertNotNull(quality);
        assertEquals(quality.getName(), "A");
        quality.setName("AA");
        environment.update(token.getKey(), a.getId(), quality);
        Quality quality2 = environment.getQuality(token.getKey(), a.getId());
        assertNotNull(quality2);
        assertEquals(quality2.getName(), "AA");
    }

    @Test
    public void delete() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        final Token token = newToken("quality");
        environment.deleteQuality(token.getKey(), a.getId());
        final List<Quality> qualities = environment.qualities(token.getKey());
        assertEquals(qualities.size(), 1L);
    }

    @Test
    public void create() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        createQuality("D", "template-d", Accessibility.normal);
        final Token token = newToken("quality");
        final Quality quality = new Quality();
        quality.setUser(successUser);
        quality.setTemplate("template");
        quality.setName("name");
        quality.setAccessibility(Accessibility.normal);
        environment.createQuality(token.getKey(), quality);
        final List<Quality> qualities = environment.qualities(token.getKey());
        assertEquals(qualities.size(), 3L);
    }
}