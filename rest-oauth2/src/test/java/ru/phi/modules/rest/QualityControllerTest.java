package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.Quality;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class QualityControllerTest extends AbstractRestTest {

    @Test
    public void notExistsDelete() throws Exception {
        final String accessToken = newToken("delete:quality");
        delete("/rest/v1/qualities/{id}", 1000L, accessToken).andExpect(status().isNotFound());
    }

    @Test
    public void notExistsGet() throws Exception {
        get("/rest/v1/qualities/{id}", 1000L).andExpect(status().isNotFound());
    }

    @Test
    public void notExistsUpdate() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        final String accessToken = newToken("write:quality");
        update("/rest/v1/qualities/{id}", 1000L, accessToken, a).andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        createQuality("C", "template-c", Accessibility.eyeless);
        createQuality("D", "template-d", Accessibility.normal);
        get("/rest/v1/qualities")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void get() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        final Quality b = createQuality("B", "template-b", Accessibility.brainless);
        get("/rest/v1/qualities/{id}", b.getId())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is(b.getName())));
    }

    @Test
    public void update() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        final String accessToken = newToken("write:quality");
        a.setName("A");
        update("/rest/v1/qualities/{id}", a.getId(), accessToken, a)
                .andExpect(status().isNoContent());
        ;
        final Quality one = qualityRepository.findOne(a.getId());
        assertEquals(one.getName(), "A");
    }

    @Test
    public void delete() throws Exception {
        final Quality a = createQuality("A", "template-a", Accessibility.baroow);
        createQuality("B", "template-b", Accessibility.brainless);
        final String accessToken = newToken("delete:quality");
        delete("/rest/v1/qualities/{id}", a.getId(), accessToken)
                .andExpect(status().isNoContent());
    }

    @Test
    public void create() throws Exception {
        createQuality("A", "template-a", Accessibility.baroow);
        createQuality("D", "template-d", Accessibility.normal);
        final String accessToken = newToken("write:quality");
        final Quality quality = new Quality();
        quality.setName("C");
        quality.setTemplate("template-c");
        quality.setAccessibility(Accessibility.brainless);
        create("/rest/v1/qualities", accessToken, quality)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is("C")));
        assertEquals(qualityRepository.count(), 3L);
    }
}