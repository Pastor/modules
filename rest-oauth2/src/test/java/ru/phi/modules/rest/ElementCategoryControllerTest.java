package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.phi.modules.entity.ElementCategory;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ElementCategoryControllerTest extends AbstractRestTest {

    @Test
    public void notExistsGet() throws Exception {
        get("/rest/v1/categories/{id}", 10000L)
                .andExpect(status().isNotFound());
    }

    @Test
    public void notExistsUpdate() throws Exception {
        final String accessToken = newToken("write:category");
        final ElementCategory category = new ElementCategory();
        category.setName("NAME1");
        category.setIcon("ICON1");
        update("/rest/v1/categories/{id}", 1000L, accessToken, category)
                .andExpect(status().isNotFound());
    }

    @Test
    public void notExistsDelete() throws Exception {
        final String accessToken = newToken("delete:category");
        delete("/rest/v1/categories/{id}", 1000L, accessToken)
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        createCategory(successUser, "Переход");
        get("/rest/v1/categories")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(registeredCategory() + 1)));
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        get("/rest/v1/categories")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(registeredCategory() + 3)));
    }

    @Test
    public void create() throws Exception {
        final String accessToken = newToken("write:category");
        final ElementCategory category = new ElementCategory();
        category.setName("NAME1");
        category.setIcon("ICON1");
        create("/rest/v1/categories", accessToken, category)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        assertEquals(elementCategoryRepository.count(), 1 + registeredCategory());
    }

    @Test
    public void get() throws Exception {
        final ElementCategory category = createCategory(successUser, "Переход");
        final ResultActions expect = get("/rest/v1/categories/{id}", category.getId())
                .andExpect(status().isOk());
        final ElementCategory elementCategory = toObject(expect, ElementCategory.class);
        assertEquals(category, elementCategory);
    }

    @Test
    public void update() throws Exception {
        final String accessToken = newToken("write:category");
        final ElementCategory category = createCategory(successUser, "Переход");
        category.setName("NAME1");
        update("/rest/v1/categories/{id}", category.getId(), accessToken, category)
                .andExpect(status().isNoContent());
        assertEquals(elementCategoryRepository.count(), 1 + registeredCategory());
        assertEquals(elementCategoryRepository.findOne(category.getId()).getName(), "NAME1");
    }

    @Test
    public void delete() throws Exception {
        final String accessToken = newToken("delete:category");
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        final ElementCategory category = createCategory(successUser, "NAME3");
        createCategory(successUser, "NAME4");
        delete("/rest/v1/categories/{id}", category.getId(), accessToken)
                .andExpect(status().isNoContent());
        assertEquals(elementCategoryRepository.count(), 3 + registeredCategory());
    }

    @Test
    public void count() throws Exception {
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        final String content = get("/rest/v1/categories/count")
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(Long.parseLong(content), 2 + registeredCategory());
    }
}