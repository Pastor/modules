package ru.phi.modules;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;
import ru.phi.modules.security.FilterConfiguration;
import ru.phi.modules.security.RestSecurityConfiguration;

import javax.servlet.Filter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        classes = {
                RestSecurityConfiguration.class,
                RestMvcConfiguration.class,
                JpaConfiguration.class,
                FilterConfiguration.class
        },
        loader = AnnotationConfigWebContextLoader.class)
@TestPropertySource({"classpath:application.properties"})
//@SqlGroup({
//        @Sql(scripts = {"/sql/data/create.users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(
//                dataSource = "dataSource", transactionManager = "emTransactionManager", encoding = "UTF-8"
//        )),
//        @Sql(scripts = {"/sql/data/drop.users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(
//                dataSource = "dataSource", transactionManager = "emTransactionManager", encoding = "UTF-8"
//        ))
//})
public class SecurityRestControllerTest {

    @Autowired
    @Qualifier("securityFilter")
    private Filter securityFilter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(securityFilter, "/*").build();
        final User user = new User();
        user.setPassword("123456");
        user.setUsername("pastor");
        userRepository.save(user);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void faultLogin() throws Exception {
        mockMvc.perform(post("/rest/v1/login")
                .param("login", "username")
                .param("password", "password")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void emptyLogin() throws Exception {
        mockMvc.perform(post("/rest/v1/login")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void successLogin() throws Exception {
        mockMvc.perform(post("/rest/v1/login")
                .param("login", "pastor")
                .param("password", "123456")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("expected_at").isNotEmpty());
    }

    @Test(expected = BadCredentialsException.class)
    public void faultToken() throws Exception {
        mockMvc.perform(get("/rest/v1/user")
                .param("token", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test(expected = NestedServletException.class)
    public void emptyToken() throws Exception {
        mockMvc.perform(get("/rest/v1/user")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void successToken() throws Exception {
        final User user = userRepository.find("pastor", "123456");
        final Token token = new Token();
        final String key = UUID.randomUUID().toString();
        token.setKey(key);
        token.setExpiredAt(LocalDateTime.now().plus(10, ChronoUnit.HOURS));
        token.setUser(user);
        tokenRepository.save(token);
        mockMvc.perform(get("/rest/v1/user")
                .param("token", key)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("pastor"));
    }
}