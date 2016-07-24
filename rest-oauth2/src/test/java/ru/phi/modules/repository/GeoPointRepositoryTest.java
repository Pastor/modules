package ru.phi.modules.repository;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.entity.GeoPoint;
import ru.phi.modules.rest.AbstractRestTest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

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
public final class GeoPointRepositoryTest extends AbstractRestTest {
    @Test
    public void testEquals() throws Exception {
        assertEquals(
                point(34.00000, 56.000000),
                point(34.00000, 56.000000)
        );
        assertNotSame(
                point(34.00000, 57.000000),
                point(34.00000, 56.000000)
        );
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(
                point(34.00000, 56.000000).hashCode(),
                point(34.00000, 56.000000).hashCode()
        );
        assertNotSame(
                point(34.00000, 57.000000).hashCode(),
                point(34.00000, 56.000000).hashCode()
        );
    }

    @Ignore
    @Test
    public void listElements() throws Exception {
        createElement(successUser, "NAME1", "NAME1", "ADDRESS1", 68.00000, 78.00000, hospital, standardAccPro());
        createElement(successUser, "NAME2", "NAME2", "ADDRESS2", 68.00000, 78.00000, hospital, standardAccPro());
        createElement(successUser, "NAME3", "NAME3", "ADDRESS3", 68.00000, 78.00000, hospital, standardAccPro());
        createElement(successUser, "NAME4", "NAME4", "ADDRESS4", 68.00000, 78.00000, hospital, standardAccPro());
        assertEquals(geoPointRepository.count(), 1);
        final GeoPoint point = geoPointRepository.findAll().iterator().next();
        assertEquals(point.getElements().size(), 4);
    }
}