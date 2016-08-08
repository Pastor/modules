package ru.phi.modules.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import ru.phi.modules.FakeApplication;
import ru.phi.modules.Utilities;
import ru.phi.modules.entity.*;
import ru.phi.modules.oauth2.AuthorizationServerConfiguration;
import ru.phi.modules.repository.*;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.phi.modules.Constants.CLIENT_ID;
import static ru.phi.modules.Constants.CLIENT_SECRET;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = {FakeApplication.class})
@TestPropertySource({"classpath:application.properties"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("oauth2")
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
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

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

    protected MockMvc mvc;

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
        successUser.setRole(UserRole.admin);
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

        hospital = createCategory(successUser, "Поликлиника");
        emergency = createCategory(successUser, "Станция скорой медицинской помощи");
        registeredCategory = elementCategoryRepository.count();

        Utilities.register(accessibilityProcessRepository);

        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain).build();
    }

    protected final int registeredCategory() {
        return (int) registeredCategory;
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
        newsRepository.deleteAll();
        qualityRepository.deleteAll();
        elementCategoryRepository.deleteAll();
        accessibilityProcessRepository.deleteAll();
    }

    public final <E> E toObject(ResultActions resultActions, Class<E> klass) throws Exception {
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), klass);
    }

    public final ResultActions get(String template) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template)
                .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    public final ResultActions getContent(String template) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template)
                .accept(MediaType.ALL));
    }

    public final ResultActions getContent(String template, String accessToken) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template)
                .accept(MediaType.ALL)
                .header("Authorization", "Bearer " + accessToken));
    }

    public final ResultActions get(String template, int page, int size) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    public final ResultActions get(String template, long id) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template, id)
                .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    public final ResultActions get(String template, String accessToken) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken));
    }

    public final ResultActions get(String template, long id, String accessToken) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(template, id)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken));
    }

    public final ResultActions delete(String template, long id, String accessToken) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(template, id)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken));
    }

    public final <E> ResultActions update(String template, long id, String accessToken, E object) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(template, id)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(object)));
    }

    public final <E> ResultActions update(String template, String accessToken, E object) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(template)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(object)));
    }

    public final <E> ResultActions create(String template, String accessToken, E object) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(template)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(object)));
    }

    protected final String newToken(String... scopes) throws Exception {
        return accessToken(successUser.getUsername(), successUser.getPassword(), scopes);
    }

    protected final String newTokenWithoutProfile(String... scopes) throws Exception {
        return accessToken(successUserWithoutProfile.getUsername(), successUserWithoutProfile.getPassword(), scopes);
    }

    protected final String accessToken(String username, String password, String... scopes) throws Exception {
        String authorization = "Basic "
                + new String(Base64Utils.encode((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));
        String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

        // @formatter:off
        String content = mvc
                .perform(
                        post("/oauth/token")
                                .header("Authorization", authorization)
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .param("grant_type", "password")
//                                .param("scope", "read:user write:user")
                                .param("scope", Joiner.on(' ').join(scopes))
                                .param("client_id", CLIENT_ID)
                                .param("client_secret", password(CLIENT_SECRET)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
                .andExpect(jsonPath("$.refresh_token", is(notNullValue())))
                .andExpect(jsonPath("$.expires_in", is(greaterThan(3000))))
                .andExpect(jsonPath("$.scope", is(equalTo(Joiner.on(' ').join(scopes)))))
                .andReturn().getResponse().getContentAsString();

        // @formatter:on

        return content.substring(17, 53);
    }

    private static String password(String password) {
        return AuthorizationServerConfiguration.passwordEncoder.encode(password);
    }

    protected final ElementCategory createCategory(User user, String name) {
        final ElementCategory category = new ElementCategory();
        category.setUser(user);
        category.setName(name);
        return elementCategoryRepository.save(category);
    }

    protected final GeoPoint point(User user, GeoPoint point) {
        if (point == null)
            return null;
        return point(user, point.getLatitude(), point.getLongitude());
    }

    protected final GeoPoint point(double latitude, double longitude) {
        return point(successUser, latitude, longitude);
    }

    protected final GeoPoint point(User user, double latitude, double longitude) {
        return Utilities.point(geoPointRepository, user, latitude, longitude);
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
        element.setUuid(UUID.randomUUID().toString());
        return elementRepository.save(element);
    }

    protected final EndPoint createEndpoint(User user,
                                            double latitude,
                                            double longitude,
                                            EndPointType type,
                                            AccessibilityProcess... processes) {
        final EndPoint endPoint = new EndPoint();
        endPoint.setUser(user);
        endPoint.setPoint(point(user, latitude, longitude));
        endPoint.setType(type);
        endPoint.setAccessibility(Sets.newHashSet(processes));
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

    protected final AccessibilityProcess standardAccPro() {
        return Utilities.standard(accessibilityProcessRepository);
    }
}
