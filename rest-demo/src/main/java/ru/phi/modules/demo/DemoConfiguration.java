package ru.phi.modules.demo;

import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.entity.*;
import ru.phi.modules.repository.*;
import ru.phi.modules.security.RestSecurityConfiguration;
import ru.phi.modules.security.SecurityUtilities;
import ru.phi.modules.strong.StaticConfiguration;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
@Import({
        JpaConfiguration.class,
        RestMvcConfiguration.class,
        RestSecurityConfiguration.class,
        StaticConfiguration.class
})
@DependsOn({"accessibilityController.v1"})
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
    private ScopeRepository scopeRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessibilityProcessRepository acp;

    @Autowired
    private GeoPointRepository geoPointRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    @Transactional
    @PostConstruct
    private void construct() {
        log.info("Создание областей доступа");
        final Scope scopeProfile = registerScope("profile");
        final Scope scopeSettings = registerScope("settings");
        final Scope scopePing = registerScope("ping");
        final Scope scopeElement = registerScope("element");
        final Scope scopeCategory = registerScope("category");
        final Scope scopeQuality = registerScope("quality");
        final Scope scopeNews = registerScope("news");
        final Scope scopeStatistic = registerScope("statistic");
        final Scope scopeError = registerScope("error");
        log.info("Создание пользователей");
        final User pastor = createUser("pastor", "+79265943742", "123456",
                "viruszold@mail.ru", UserRole.admin);
        final User vasia = createUser("vasia", "+79265940000", "123456",
                "vasia@mail.ru", UserRole.user);
        final User content = createUser("content", "+79265941111", "123456",
                "content@mail.ru", UserRole.content);
        log.info("Сщздание токенов");
        createToken(pastor, scopeCategory, scopeElement, scopeNews, scopePing, scopeProfile, scopeQuality,
                scopeSettings, scopeStatistic, scopeError);
        createToken(vasia, scopeProfile, scopeSettings);
        createToken(content, scopeProfile, scopeSettings, scopeNews);
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
                "Станция скорой медицинской помощи",
                "МБУЗ Химкинская станция скорой медицинской помощи",
                "141400, МО, г. Химки, ул. Молодежная д. 9",
                56.00000000,
                53.00000000,
                emergency,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.condition),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        registerPolygon(
                element,
                point(pastor, 87.00000, 45.000000),
                point(pastor, 87.10000, 45.100000),
                point(pastor, 87.20000, 45.200000),
                point(pastor, 87.30000, 45.300000),
                point(pastor, 87.40000, 45.400000),
                point(pastor, 87.50000, 45.500000)
        );
        element = createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", педиатрический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        final AccessibilityProcess standard = ru.phi.modules.Utilities.standard(acp);
        final EndPoint point1 = createEndPoint(pastor, point(pastor, 34.00000, 76.000000), EndPointType.exit, standard);
        final EndPoint point2 = createEndPoint(pastor, point(pastor, 34.60000, 76.009000), EndPointType.enter, standard);
        element.setEndPoints(Sets.newHashSet(point1, point2));
        elementRepository.save(element);
        element = createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", травмотолого-ортопедическое отделение полеклинники",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        element = createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", хирургический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.baroow, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.legless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.eyeless, AccessibilityType.not_information),
                acp.findByAccessibilityAndType(Accessibility.brainless, AccessibilityType.not_information)
        );
        element = createElement(
                pastor,
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
        log.info("Окончание");
    }

    private void createToken(User user, Scope... scopes) {
        final Token token = new Token();
        token.setUser(user);
        token.setExpiredAt(LocalDateTime.now().plus(365, ChronoUnit.DAYS));
        token.setScopes(Sets.newHashSet(scopes));
        final String key = SecurityUtilities.generateTokenKey();
        token.setKey(key);
        log.info("Token for {}: {}", user.getUsername(), key);
        tokenRepository.save(token);
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

    private Scope registerScope(String scopeName) {
        final Scope scope = new Scope();
        scope.setName(scopeName);
        scope.setRole(UserRole.user);
        log.info(MessageFormat.format("Создана область {0}", scopeName));
        return scopeRepository.save(scope);
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
}
