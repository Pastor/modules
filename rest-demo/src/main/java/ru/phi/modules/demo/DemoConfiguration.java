package ru.phi.modules.demo;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.RestMvcConfiguration;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.repository.*;
import ru.phi.modules.security.RestSecurityConfiguration;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;

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

    @Transactional
    @PostConstruct
    private void construct() {
        log.info("Создание областей доступа");
        createScopes();
        log.info("Создание пользователей");
        final User pastor = createUser("pastor", "+79265943742", "123456",
                "viruszold@mail.ru", UserRole.ADMIN);
    }

    private void createScopes() {
        registerScope("profile");
        registerScope("settings");
        registerScope("ping");
        registerScope("element");
        registerScope("categories");
        registerScope("quality");
        registerScope("news");
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

    private void registerScope(String scopeName) {
        final Scope scope = new Scope();
        scope.setName(scopeName);
        scope.setRole(UserRole.USER);
        scopeRepository.save(scope);
        log.info(MessageFormat.format("Создана область {0}", scopeName));
    }
}
