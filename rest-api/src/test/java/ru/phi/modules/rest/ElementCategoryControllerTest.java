package ru.phi.modules.rest;

import com.google.common.collect.Lists;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.ElementCategory;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.ObjectNotFoundException;

import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ElementCategoryControllerTest extends AbstractRestTest {

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsGet() throws Exception {
        final Token token = newToken("category");
        environment.getCategory(token.getKey(), 1111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsUpdate() throws Exception {
        final Token token = newToken("category");
        final ElementCategory category = new ElementCategory();
        category.setName("NAME1");
        category.setIcon("ICON1");
        environment.update(token.getKey(), 1111L, category);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsDelete() throws Exception {
        final Token token = newToken("category");
        environment.deleteCategory(token.getKey(), 1111L);
    }

    @Test
    public void list() throws Exception {
        final Token token = newToken("category");
        createCategory(successUser, "Переход");
        final List<ElementCategory> categories = environment.categories(token.getKey());
        assertEquals(categories.size(), 1 + registeredCategory());
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        final List<ElementCategory> categories2 = environment.categories(token.getKey());
        assertEquals(categories2.size(), 3 + registeredCategory());

    }

    @Test
    public void create() throws Exception {
        final Token token = newToken("category");
        final ElementCategory category = new ElementCategory();
        category.setName("NAME1");
        category.setIcon("ICON1");
        final ElementCategory category2 = environment.createCategory(token.getKey(), category);
        final List<ElementCategory> categories = Lists.newArrayList(elementCategoryRepository.findAll());
        assertEquals(categories.size(), 1 + registeredCategory());
        assertEquals(category2.getName(), "NAME1");
    }

    @Test
    public void get() throws Exception {
        final Token token = newToken("category");
        final ElementCategory category = createCategory(successUser, "Переход");
        final ElementCategory category2 = environment.getCategory(token.getKey(), category.getId());
        assertEquals(category, category2);
    }

    @Test
    public void update() throws Exception {
        final Token token = newToken("category");
        final ElementCategory category = createCategory(successUser, "Переход");
        category.setName("NAME1");
        environment.update(token.getKey(), category.getId(), category);
        final List<ElementCategory> categories = Lists.newArrayList(elementCategoryRepository.findAll());
        assertEquals(categories.size(), 1 + registeredCategory());
        assertEquals(categories.get(registeredCategory()).getName(), "NAME1");
    }

    @Test
    public void delete() throws Exception {
        final Token token = newToken("category");
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        final ElementCategory category = createCategory(successUser, "NAME3");
        createCategory(successUser, "NAME4");
        environment.deleteCategory(token.getKey(), category.getId());
        final List<ElementCategory> categories = Lists.newArrayList(elementCategoryRepository.findAll());
        assertEquals(categories.size(), 3 + registeredCategory());
    }

    @Test
    public void count() throws Exception {
        final Token token = newToken("category");
        createCategory(successUser, "NAME1");
        createCategory(successUser, "NAME2");
        final Long count = environment.categoriesCount(token.getKey());
        assertEquals(count.longValue(), 2 + registeredCategory());
    }
}