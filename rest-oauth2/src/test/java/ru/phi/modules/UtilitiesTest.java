package ru.phi.modules;

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
import ru.phi.modules.entity.GeoPoint;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.exceptions.SystemException;
import ru.phi.modules.repository.GeoPointRepository;
import ru.phi.modules.repository.UserRepository;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

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
public final class UtilitiesTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeoPointRepository geoPointRepository;

    @Test
    public void pointWithGeo() throws Exception {
        final User user = registerUser();
        final GeoPoint point = new GeoPoint();
        point.setLatitude(90.00000);
        point.setLongitude(87.0000000);
        final GeoPoint geoPoint = Utilities.point(geoPointRepository, user, point);
        assertNotNull(geoPoint.getUser());
        assertNotNull(geoPoint.getId());
    }


    @Test(expected = SystemException.class)
    public void pointWithUserNull() throws Exception {
        final GeoPoint point = new GeoPoint();
        point.setLatitude(90.00000);
        point.setLongitude(87.0000000);
        assertNull(Utilities.point(geoPointRepository, null, point));
    }

    @Test(expected = SystemException.class)
    public void pointWithRepositoryNull() throws Exception {
        final User user = registerUser();
        final GeoPoint point = new GeoPoint();
        point.setLatitude(90.00000);
        point.setLongitude(87.0000000);
        assertNull(Utilities.point(null, user, point));
    }

    @Test
    public void pointWithPointNull() throws Exception {
        final User user = registerUser();
        assertNull(Utilities.point(geoPointRepository, user, null));
    }

    @Test
    public void entityColumnLength() throws Exception {
        Utilities.entityColumnLength(ru.phi.modules.entity.Error.class, "trace");
    }

    @Test(expected = RuntimeException.class)
    public void faultEntityColumnLength() throws Exception {
        Utilities.entityColumnLength(ru.phi.modules.entity.User.class, "trace");
    }

    private User registerUser() {
        final User user = new User();
        user.setEmail("e@m.ru");
        user.setRole(UserRole.user);
        user.setPhone("000000000");
        user.setUsername("username");
        user.setPassword("00000000000000000000000");
        return userRepository.save(user);
    }
}