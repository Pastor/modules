package ru.phi.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.entity.*;
import ru.phi.modules.repository.*;
import ru.phi.modules.security.Environment;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource({"classpath:application.properties"})
public abstract class AbstractRestTest {
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
    protected ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    protected Environment environment;
    protected final User successUser = new User();
    protected final Profile successProfile = new Profile();

    @Before
    public void setUp() {
        userRepository.deleteAll();
        successUser.setPassword("123456");
        successUser.setUsername("pastor");
        userRepository.save(successUser);
        successProfile.setUser(successUser);
        successProfile.setAccessibility(Accessibility.BAROOW);
        successProfile.setEmail("viruszold@mail.ru");
        successProfile.setFirstName("Иванов");
        successProfile.setLastName("Михаил");
        profileRepository.save(successProfile);
        environment = new Environment(objectMapper, port);
        register("profile");
        register("settings");
    }

    protected final void register(String scopeName) {
        final Scope scope = new Scope();
        scope.setName(scopeName);
        scope.setRole(UserRole.USER);
        scopeRepository.save(scope);
    }

    @After
    public void tearDown() throws Exception {
        errorRepository.deleteAll();
        settingsRepository.deleteAll();
        profileRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        environment.clearDown();
    }

    protected final Token newToken(String... scopes) {
        return environment.postUpdate(successUser.getUsername(), successUser.getPassword(), scopes);
    }
}
