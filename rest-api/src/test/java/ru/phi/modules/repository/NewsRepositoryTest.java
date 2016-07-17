package ru.phi.modules.repository;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@ContextConfiguration(classes = {JpaConfiguration.class})
@TestPropertySource({"classpath:application.properties"})
@SqlGroup({
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        dataSource = "dataSource",
                        encoding = "UTF-8"
                )
        )
})
public class NewsRepositoryTest {
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;


    @After
    public void tearDown() throws Exception {
        newsRepository.deleteAll();
        userRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Transactional
    @Test(expected = RuntimeException.class)
    public void createNews() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user = userRepository.save(user);
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setLastName("Last");
        profile.setFirstName("First");
        profile.setAccessibility(Accessibility.EYELESS);
        profile.setEmail("m@me.com");
        profile = profileRepository.save(profile);
        final News news = new News();
        news.setTitle("Title");
        news.setBref("Bref");
        news.setProfile(profile);
        newsRepository.save(news);
        throw new RuntimeException();
    }
}