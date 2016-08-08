package ru.phi.modules.demo;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import ru.phi.modules.Constants;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.entity.*;
import ru.phi.modules.oauth2.AuthorizationServerConfiguration;
import ru.phi.modules.oauth2.RestMvcConfiguration;
import ru.phi.modules.repository.*;
import ru.phi.modules.strong.StaticConfiguration;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ru.phi.modules.Constants.*;

/**
 * todo: 3. Посмотреть, как настраивать профили через Lua-скрипты
 * todo: 4. Для bicycle.lua - поправить отображение поворотов на улицы с неизвестными именами
 */
@Slf4j
@Configuration
@Import({
        JpaConfiguration.class,
        RestMvcConfiguration.class,
        StaticConfiguration.class
})
@org.springframework.context.annotation.Profile("oauth2")
@ComponentScan(basePackages = {"ru.phi.modules.oauth2"})
@DependsOn({"accessibilityController.v1", "authorizationServerConfiguration.v1"})
public class DemoConfiguration {

    private static final HashFunction hash = Hashing.goodFastHash(256);

    @Autowired
    private ElementCategoryRepository elementCategoryRepository;

    @Autowired
    private ElementRepository elementRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private QualityRepository qualityRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessibilityProcessRepository acp;

    @Autowired
    private GeoPointRepository geoPointRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Transactional
    @PostConstruct
    private void construct() {
        log.info("Создание пользователей");
        final User pastor = createUser("pastor", "+79265943742", "123456",
                "viruszold@mail.ru", UserRole.admin);
        final User vasia = createUser("vasia", "+79265940000", "123456",
                "vasia@mail.ru", UserRole.user);
        final User content = createUser("content", "+79265941111", "123456",
                "content@mail.ru", UserRole.content);
        log.info("Сщздание токенов");
        log.info("Создание профилей для пользователей");
        final Profile vasiaProfile = createProfile(vasia, "Залупа", "Василий", "Николаевич", Accessibility.baroow);
        final Profile pastorProfile = createProfile(pastor, "Хлебников", "Андрей", "Александрович", Accessibility.normal);
        final Profile contentProfile = createProfile(content, "Контент", "Михаил", "Андреевич", Accessibility.eyeless);
        log.info("Создание шаблонов UI");
        createQuality(pastor, "Нормальный", "normal", Accessibility.normal);
        createQuality(pastor, "Слабовидещие", "eyeless", Accessibility.eyeless);
        log.info("Создание категорий");
        final ElementCategory hospital = createCategory(pastor, "Поликлиника");
        final ElementCategory emergency = createCategory(pastor, "Станция скорой медицинской помощи");
        final ElementCategory underground = createCategory(pastor, "Подземный переход");
        log.info("Создание объектов инфраструктуры");
        Element element;
        element = createElement(
                pastor,
                "6f414da8-2a75-4840-bc90-87e8422099d9",
                "Станция скорой медицинской помощи",
                "МБУЗ Химкинская станция скорой медицинской помощи",
                "141400, МО, г. Химки, ул. Молодежная д. 9",
                55.88178,
                37.41662,
                emergency,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        registerPolygon(
                element,
                point(pastor, 55.88169479, 37.41653824),
                point(pastor, 55.88175583, 37.41675186),
                point(pastor, 55.88210678, 37.41642761),
                point(pastor, 55.88205338, 37.41624069),
                point(pastor, 55.88191605, 37.41636658),
                point(pastor, 55.88187408, 37.41622162),
                point(pastor, 55.88201904, 37.41609192),
                point(pastor, 55.88194275, 37.4158287),
                point(pastor, 55.88158798, 37.41615677),
                point(pastor, 55.88166428, 37.41641998),
                point(pastor, 55.88180161, 37.4162941),
                point(pastor, 55.88183594, 37.41640854),
                point(pastor, 55.88169479, 37.41653824)
        );
        element = createElement(
                pastor,
                "ce758060-3116-4dea-bf72-95d63baf07f3",
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", педиатрический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                55.88182068,
                37.41544724,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        registerPolygon(
                element,
                point(pastor, 55.88182068, 37.41544724),
                point(pastor, 55.88146973, 37.41579819),
                point(pastor, 55.88121414, 37.41498566),
                point(pastor, 55.88156891, 37.41463089),
                point(pastor, 55.88171387, 37.41509247),
                point(pastor, 55.88158417, 37.41522217),
                point(pastor, 55.88147736, 37.41487885),
                point(pastor, 55.88138962, 37.41496658),
                point(pastor, 55.8814888, 37.41528702),
                point(pastor, 55.88142776, 37.41535187),
                point(pastor, 55.88146973, 37.41548538),
                point(pastor, 55.8817482, 37.41521072),
                point(pastor, 55.88182068, 37.41544724)
        );
        final AccessibilityProcess standard = ru.phi.modules.Utilities.standard(acp);
        final EndPoint point1 = createEndPoint(pastor, point(pastor, 34.00000, 76.000000), EndPointType.exit, standard);
        final EndPoint point2 = createEndPoint(pastor, point(pastor, 34.60000, 76.009000), EndPointType.enter, standard);
        element.setEndPoints(Sets.newHashSet(point1, point2));
        elementRepository.save(element);
        element = createElement(
                pastor,
                "d1b99e3f-184d-436f-ae2a-ac57ec8224f9",
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", травмотолого-ортопедическое отделение полеклинники",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                55.88243103,
                37.41345596,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        registerPolygon(
                element,
                point(pastor, 55.88243103, 37.41345596),
                point(pastor, 55.88243103, 37.41405106),
                point(pastor, 55.88291931, 37.41404724),
                point(pastor, 55.88291931, 37.41344833),
                point(pastor, 55.88243103, 37.41345596)
        );
        element = createElement(
                pastor,
                "67e02cff-2634-4510-b8e2-ee16f7506853",
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", хирургический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                55.8832283,
                37.41239166,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        registerPolygon(
                element,
                point(pastor, 55.8832283, 37.41239166),
                point(pastor, 55.88344193, 37.41238785),
                point(pastor, 55.88345337, 37.41345978),
                point(pastor, 55.88323975, 37.41346359),
                point(pastor, 55.88323593, 37.41310501),
                point(pastor, 55.88320923, 37.41310501),
                point(pastor, 55.88320923, 37.41294861),
                point(pastor, 55.88323593, 37.41294861),
                point(pastor, 55.8832283, 37.41239166)
        );
        element = createElement(
                pastor,
                UUID.randomUUID().toString(),
                "Переход",
                "",
                "ул. Зои Космедемьянской, д. 9/12",
                56.00000000,
                53.00000000,
                underground
        );
        log.info("Создание новостей");
        createNews(pastorProfile, "Первая версия API", "Первая версия API", "<h1>ПЕРВАЯ ВЕРСИЯ API</h1>");
        createNews(pastorProfile, "Вторая версия API", "Вторая версия API", "<h1>ВТОРАЯ ВЕРСИЯ API</h1>");
        createNews(pastorProfile, "Третья версия API", "Третья версия API", "<h1>ТРЕТЬЯ ВЕРСИЯ API</h1>");

        final Version v = new Version();
        v.setMajor(1L);
        v.setMinor(0L);
        v.setBuild(3L);
        v.setRc(Boolean.TRUE);
        v.setSupport("support@me.com");
        versionRepository.save(v);

        log.info(MessageFormat.format("client-id: {0}", CLIENT_ID));
        log.info(MessageFormat.format("client-secret: {0}", password(CLIENT_SECRET)));
        log.info(MessageFormat.format("user-password[pastor]: {0}", hash("123456")));
        log.info(MessageFormat.format("user-access-token[pastor]: {0}", accessToken(pastor, Constants.CLIENT_SCOPES)));
        log.info(MessageFormat.format("authorization: {0}", "Basic " + new String(Base64Utils.encode((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))));

        log.info("Окончание");
    }

    private News createNews(Profile profile, String title, String bref, String content) {
        final News news = new News();
        news.setProfile(profile);
        news.setTitle(title);
        news.setBref(bref);
        news.setContent(content);
        news.setVisible(true);
        news.setPublishedAt(LocalDateTime.now());
        final News save = newsRepository.save(news);
        log.info("Создана новость {}", save);
        return save;
    }

    private Element createElement(User user,
                                  String uuid,
                                  String name,
                                  String fullName,
                                  String address,
                                  double latitude,
                                  double longitude,
                                  ElementCategory category,
                                  AccessibilityProcess... processes) {
        final Element element = new Element();
        element.setUuid(uuid);
        element.setUser(user);
        element.setName(name);
        element.setFullName(fullName);
        element.setAddress(address);
        final GeoPoint point = point(user, latitude, longitude);
        element.setPoint(point);
        final AccessibilityProcess standard = ru.phi.modules.Utilities.standard(acp);
        element.setEndPoints(Sets.newHashSet(createEndPoint(user, point, EndPointType.both, standard)));
        element.setCategories(Sets.newHashSet(category));
        element.setAccessibilityProcesses(Sets.newHashSet(processes));
        final Element save = elementRepository.save(element);
        log.info("Создан объект {}", save);
        return save;
    }

    private GeoPoint point(User user, double latitude, double longitude) {
        return ru.phi.modules.Utilities.point(geoPointRepository, user, latitude, longitude);
    }

    private ElementCategory createCategory(User user, String name) {
        final ElementCategory category = new ElementCategory();
        category.setUser(user);
        category.setName(name);
        final ElementCategory save = elementCategoryRepository.save(category);
        log.info("Создана категория \"{}\"", category);
        return save;
    }

    private void createQuality(User user, String name, String template, Accessibility accessibility) {
        final Quality quality = new Quality();
        quality.setAccessibility(accessibility);
        quality.setUser(user);
        quality.setTemplate(template);
        quality.setName(name);
        final Quality save = qualityRepository.save(quality);
        log.info("Создане шаблон \"{}\"", save);
    }

    private Profile createProfile(User user,
                                  String lastName,
                                  String firstName,
                                  String middleName,
                                  Accessibility accessibility) {
        final Profile profile = new Profile();
        profile.setUser(user);
        profile.setLastName(lastName);
        profile.setFirstName(firstName);
        profile.setMiddleName(middleName);
        profile.setCity("Москва");
        profile.setEmail(user.getEmail());
        profile.setAccessibility(accessibility);
        profile.setAddress("ул. Удальцова, д. 85 к. 1/5");
        final Profile save = profileRepository.save(profile);
        log.info(MessageFormat.format("Создан профиль {0}", profile));
        return save;
    }

    private User createUser(String username,
                            String phone,
                            String password,
                            String email,
                            UserRole role) {
        final User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(hash(password));
        user.setEmail(email);
        user.setRole(role);
        final User save = userRepository.save(user);
        log.info(MessageFormat.format("Создан пользователь {0}", save));
        return save;
    }

    private static String hash(String text) {
        return hash.hashUnencodedChars(text).toString().toUpperCase();
    }

    private Element registerPolygon(Element element, GeoPoint... points) {
        element.setPolygon(Sets.newHashSet(points));
        return elementRepository.save(element);
    }

    private EndPoint createEndPoint(User user, GeoPoint point, EndPointType type, AccessibilityProcess... processes) {
        final EndPoint endPoint = new EndPoint();
        endPoint.setUser(user);
        endPoint.setType(type);
        endPoint.setPoint(point);
        endPoint.setAccessibility(Sets.newHashSet(processes));
        return endPointRepository.save(endPoint);
    }

    private static String password(String password) {
        return AuthorizationServerConfiguration.passwordEncoder.encode(password);
    }

    @Autowired
    private AuthorizationServerTokenServices tokenService;

    private String accessToken(User user, String... scopes) {
        final Map<String, String> params = Maps.newConcurrentMap();
        params.put("scope", Joiner.on(' ').join(scopes));
        params.put("username", user.getUsername());
        params.put("password", hash(user.getPassword()));
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", password(CLIENT_SECRET));
        params.put("grant_type", "password");
        final Set<UserRole> authorities = Sets.newHashSet(user.getRole());
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, authorities);
        token.setDetails(user);
        final OAuth2Authentication authentication = new OAuth2Authentication(new OAuth2Request(
                params,
                CLIENT_ID,
                authorities,
                true,
                Sets.newHashSet(scopes),
                Sets.newHashSet(RESOURCE_ID),
                "http://localhost/empty",
                Sets.newHashSet("token"),
                Maps.newHashMap()),
                token);
        authentication.setDetails(user);
        authentication.setAuthenticated(true);
        final org.springframework.security.oauth2.common.OAuth2AccessToken accessToken = tokenService.createAccessToken(authentication);
        return accessToken.getValue();
    }
}
