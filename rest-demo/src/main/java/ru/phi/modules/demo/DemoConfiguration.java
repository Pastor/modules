package ru.phi.modules.demo;

import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.entity.*;
import ru.phi.modules.repository.*;
import ru.phi.modules.security.RestSecurityConfiguration;
import ru.phi.modules.security.Utilities;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
@Import({
        JpaConfiguration.class,
        RestMvcConfiguration.class,
        RestSecurityConfiguration.class
})
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
                "viruszold@mail.ru", UserRole.ADMIN);
        final User vasia = createUser("vasia", "+79265940000", "123456",
                "vasia@mail.ru", UserRole.USER);
        final User content = createUser("content", "+79265941111", "123456",
                "content@mail.ru", UserRole.CONTENT);
        log.info("Сщздание токенов");
        createToken(pastor, scopeCategory, scopeElement, scopeNews, scopePing, scopeProfile, scopeQuality,
                scopeSettings, scopeStatistic, scopeError);
        createToken(vasia, scopeProfile, scopeSettings);
        createToken(content, scopeProfile, scopeSettings, scopeNews);
        log.info("Создание профилей для пользователей");
        final Profile vasiaProfile = createProfile(vasia, "Залупа", "Василий", "Николаевич", Accessibility.BAROOW);
        final Profile pastorProfile = createProfile(pastor, "Хлебников", "Андрей", "Александрович", Accessibility.NORMAL);
        final Profile contentProfile = createProfile(content, "Контент", "Михаил", "Андреевич", Accessibility.EYELESS);
        log.info("Создание шаблонов UI");
        createQuality(pastor, "Нормальный", "normal", Accessibility.NORMAL);
        createQuality(pastor, "Слабовидещие", "eyeless", Accessibility.EYELESS);
        log.info("Создание категорий");
        final ElementCategory hospital = createCategory(pastor, "Поликлиника");
        final ElementCategory emergency = createCategory(pastor, "Станция скорой медицинской помощи");
        final ElementCategory underground = createCategory(pastor, "Подземный переход");
        log.info("Создание объектов инфраструктуры");
        registeAcessibilityProcess();
        createElement(
                pastor,
                "Станция скорой медицинской помощи",
                "МБУЗ Химкинская станция скорой медицинской помощи",
                "141400, МО, г. Химки, ул. Молодежная д. 9",
                56.00000000,
                53.00000000,
                emergency,
                acp.findByAccessibilityAndType(Accessibility.BAROOW, AccessibilityType.CONDITION),
                acp.findByAccessibilityAndType(Accessibility.LEGLESS, AccessibilityType.CONDITION),
                acp.findByAccessibilityAndType(Accessibility.EYELESS, AccessibilityType.CONDITION),
                acp.findByAccessibilityAndType(Accessibility.BRAINLESS, AccessibilityType.NOT_INFORMATION)
        );
        createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", педиатрический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.BAROOW, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.LEGLESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.EYELESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.BRAINLESS, AccessibilityType.NOT_INFORMATION)
        );
        createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", травмотолого-ортопедическое отделение полеклинники",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.BAROOW, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.LEGLESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.EYELESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.BRAINLESS, AccessibilityType.NOT_INFORMATION)
        );
        createElement(
                pastor,
                "Центральная городская больница",
                "МБУЗ \"Химкинская Центральная городская больница\", хирургический корпус",
                "МО, г. Химки, ул. Куркинское ш., д. 11",
                46.00000000,
                53.45000000,
                hospital,
                acp.findByAccessibilityAndType(Accessibility.BAROOW, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.LEGLESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.EYELESS, AccessibilityType.NOT_INFORMATION),
                acp.findByAccessibilityAndType(Accessibility.BRAINLESS, AccessibilityType.NOT_INFORMATION)
        );
        createElement(
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
        final String key = Utilities.generateTokenKey();
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
        element.setLatitude(latitude);
        element.setLongitude(longitude);
        element.setCategories(Sets.newHashSet(category));
        element.setAccessibilityProcesses(Sets.newHashSet(processes));
        final Element save = elementRepository.save(element);
        log.info("Создан объект {}", save);
        return save;
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
        scope.setRole(UserRole.USER);
        log.info(MessageFormat.format("Создана область {0}", scopeName));
        return scopeRepository.save(scope);
    }

    private void registeAcessibilityProcess() {
        for (Accessibility accessibility : Accessibility.values()) {
            for (AccessibilityType type : AccessibilityType.values()) {
                final AccessibilityProcess entity = new AccessibilityProcess();
                entity.setAccessibility(accessibility);
                entity.setType(type);
                acp.save(entity);
            }
        }
    }
}
