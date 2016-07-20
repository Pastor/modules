package ru.phi.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.entity.*;
import ru.phi.modules.repository.*;
import ru.phi.modules.security.Environment;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = FakeApplication.class)
@TestPropertySource({"classpath:application.properties"})
@SqlGroup({
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.FAIL_ON_ERROR
                )
        )
})
public abstract class AbstractRestTest {

    @Autowired
    protected GeoPointRepository geoPointRepository;

    @Autowired
    protected VersionRepository versionRepository;

    @Autowired
    protected NewsRepository newsRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ErrorRepository errorRepository;

    @Autowired
    protected TokenRepository tokenRepository;

    @Autowired
    protected ScopeRepository scopeRepository;

    @Autowired
    protected ProfileRepository profileRepository;

    @Autowired
    protected SettingsRepository settingsRepository;

    @Autowired
    protected StatisticRepository statisticRepository;

    @Autowired
    protected QualityRepository qualityRepository;

    @Autowired
    protected ElementRepository elementRepository;

    @Autowired
    protected ElementCategoryRepository elementCategoryRepository;

    @Autowired
    protected EndPointRepository endPointRepository;

    @Autowired
    protected AccessibilityProcessRepository accessibilityProcessRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    protected Environment environment;
    protected User successUser = new User();
    protected User successUserWithoutProfile = new User();
    protected Profile successProfile = new Profile();

    protected ElementCategory hospital;
    protected ElementCategory emergency;

    private long registeredCategory;

    @Before
    public void setUp() {
        successUser.setPassword("123456");
        successUser.setUsername("pastor");
        successUser = userRepository.save(successUser);

        successUserWithoutProfile.setPassword("123456");
        successUserWithoutProfile.setUsername("pastor_without");
        successUserWithoutProfile = userRepository.save(successUserWithoutProfile);

        successProfile.setUser(successUser);
        successProfile.setAccessibility(Accessibility.baroow);
        successProfile.setEmail("viruszold@mail.ru");
        successProfile.setFirstName("Иванов");
        successProfile.setLastName("Михаил");
        successProfile = profileRepository.save(successProfile);
        environment = new Environment(objectMapper, port);

        hospital = createCategory(successUser, "Поликлиника");
        emergency = createCategory(successUser, "Станция скорой медицинской помощи");

        register("profile");
        register("settings");
        register("ping");
        register("element");
        register("category");
        register("quality");
        register("news");
        register("statistic");
        register("error");

        registeredCategory = elementCategoryRepository.count();
    }

    protected final int registeredCategory() {
        return (int) registeredCategory;
    }

    protected final void register(String scopeName) {
        final Scope scope = new Scope();
        scope.setName(scopeName);
        scope.setRole(UserRole.user);
        scopeRepository.save(scope);
    }

    @After
    public void tearDown() throws Exception {
        settingsRepository.deleteAll();
        profileRepository.deleteAll();
        statisticRepository.deleteAll();
        elementRepository.deleteAll();
        userRepository.deleteAll();
        geoPointRepository.deleteAll();
        endPointRepository.deleteAll();
        errorRepository.deleteAll();
        tokenRepository.deleteAll();
        newsRepository.deleteAll();
        qualityRepository.deleteAll();
        elementCategoryRepository.deleteAll();
        if (environment != null)
            environment.clearDown();
    }

    protected final ElementCategory createCategory(User user, String name) {
        final ElementCategory category = new ElementCategory();
        category.setUser(user);
        category.setName(name);
        return elementCategoryRepository.save(category);
    }

    protected final Token newToken(String... scopes) {
        return environment.postUpdate(successUser.getUsername(), successUser.getPassword(), scopes);
    }

    protected final Token newTokenWithoutProfile(String... scopes) {
        return environment.postUpdate(successUserWithoutProfile.getUsername(), successUserWithoutProfile.getPassword(), scopes);
    }

    protected final GeoPoint point(User user, GeoPoint point) {
        if (point == null)
            return null;
        return point(user, point.getLatitude(), point.getLongitude());
    }

    protected final GeoPoint point(User user, double latitude, double longitude) {
        return ru.phi.modules.Utilities.point(geoPointRepository, user, latitude, longitude);
    }

    protected final Element createElement(User user,
                                          String name,
                                          String fullName,
                                          String address,
                                          double latitude,
                                          double longitude,
                                          ElementCategory category,
                                          AccessibilityProcess... processes) {
        final Element element = new Element();
        element.setUser(user);
        element.setName(name);
        element.setFullName(fullName);
        element.setAddress(address);
        element.setPoint(point(user, latitude, longitude));
        element.setCategories(Sets.newHashSet(category));
        element.setAccessibilityProcesses(Sets.newHashSet(processes));
        return elementRepository.save(element);
    }

    protected final EndPoint createEndpoint(User user,
                                            double latitude,
                                            double longitude,
                                            EndPointType type) {
        final EndPoint endPoint = new EndPoint();
        endPoint.setUser(user);
        endPoint.setPoint(point(user, latitude, longitude));
        endPoint.setType(type);
        return endPointRepository.save(endPoint);
    }

    protected final Quality createQuality(String name,
                                          String template,
                                          Accessibility accessibility) {
        final Quality quality = new Quality();
        quality.setAccessibility(accessibility);
        quality.setUser(successUser);
        quality.setTemplate(template);
        quality.setName(name);
        return qualityRepository.save(quality);
    }
}
