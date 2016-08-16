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
import ru.phi.modules.osm.LoadObjects;
import ru.phi.modules.repository.*;
import ru.phi.modules.strong.StaticConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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
    private void construct() throws IOException {
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
        final ElementCategory unknown = createCategory(pastor, "Не известно");
        log.info("Создание объектов инфраструктуры");
        Element element;
        for (LoadObjects.Element oe : LoadObjects.loadUpdated()) {
            element = createElement(
                    pastor,
                    oe.id,
                    "Не известно",
                    oe.text,
                    "Адрес",
                    oe.latitude,
                    oe.longitude,
                    unknown,
                    acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                    acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                    acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                    acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
            );
            if (oe.polygon == null || oe.polygon.length == 0)
                continue;
            final GeoPoint[] points = new GeoPoint[oe.polygon.length];
            for (int i = 0; i < oe.polygon.length; i++) {
                points[i] = point(pastor, oe.polygon[i].latitude, oe.polygon[i].longitude);
            }
            registerPolygon(element, points);
        }
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
