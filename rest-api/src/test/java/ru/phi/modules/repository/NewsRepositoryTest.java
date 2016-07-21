package ru.phi.modules.repository;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.phi.modules.JpaConfiguration;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Profile;
import ru.phi.modules.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@TestPropertySource({"classpath:application.properties"})
@SqlGroup({
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR
                )
        ),
        @Sql(
                scripts = {"/sql/data/drop.data.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                config = @SqlConfig(
                        encoding = "UTF-8",
                        errorMode = SqlConfig.ErrorMode.CONTINUE_ON_ERROR
                )
        )
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class NewsRepositoryTest {
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

    @Test
    public void createNews() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user = userRepository.save(user);
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setLastName("Last");
        profile.setFirstName("First");
        profile.setAccessibility(Accessibility.eyeless);
        profile.setEmail("m@me.com");
        profile = profileRepository.save(profile);
        final News news = new News();
        news.setTitle("Title");
        news.setBref("Bref");
        news.setProfile(profile);
        newsRepository.save(news);
    }
}