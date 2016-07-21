package ru.phi.modules.repository;

import com.google.common.collect.Sets;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.entity.ElementCategory;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@TestPropertySource({"classpath:application.properties"})
@SqlGroup({
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR
                )
        )
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ElementRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElementCategoryRepository elementCategoryRepository;

    @Test
    public void withCategories() throws Exception {
        final Set<ElementCategory> categories = createCategories();
        final ElementCategory category = categories.iterator().next();
        elementCategoryRepository.delete(category.getId());
        assertEquals(categories.size() - 1, elementCategoryRepository.count());
    }

    private Set<ElementCategory> createCategories() {
        final User user = new User();
        user.setEmail("e@m.ru");
        user.setRole(UserRole.user);
        user.setPhone("000000000");
        user.setUsername("username");
        user.setPassword("00000000000000000000000");
        userRepository.save(user);
        final Set<ElementCategory> categories = Sets.newHashSet();
        for (int i = 0; i < 100; ++i) {
            final ElementCategory category = new ElementCategory();
            category.setUser(user);
            category.setName("NAME" + i);
            category.setIcon("ICON" + i);
            categories.add(elementCategoryRepository.save(category));
        }
        return categories;
    }
}