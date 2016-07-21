package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.TestCase.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class NewsControllerTest extends AbstractRestTest {

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsUpdate() throws Exception {
        final Token token = newToken("news");
        final News nn = new News();
        nn.setTitle("B");
        nn.setBref("P");
        environment.update(token.getKey(), 11111L, nn);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsPutContent() throws Exception {
        final Token token = newToken("news");
        environment.putContent(token.getKey(), 11111L, "HELLO");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsGetContent() throws Exception {
        final Token token = newToken("news");
        environment.getContent(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsHide() throws Exception {
        final Token token = newToken("news");
        environment.hide(token.getKey(), 11111L);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void notExistsPublish() throws Exception {
        final Token token = newToken("news");
        environment.publish(token.getKey(), 11111L);
    }

    @Test
    public void update() throws Exception {
        final Token token = newToken("news");
        final News nn = new News();
        nn.setTitle("B");
        nn.setBref("P");
        Long id = environment.createNews(token.getKey(), nn).getId();
        final News news = new News();
        news.setTitle("Title");
        news.setBref("BREF");
        environment.update(token.getKey(), id, news);
        final News n = environment.getNews(token.getKey(), id);
        assertEquals(n.getTitle(), "Title");
    }

    @Test
    public void putContent() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final News n = environment.createNews(token.getKey(), news);
        environment.putContent(token.getKey(), n.getId(), "HELLO");
        final String content = environment.getContent(token.getKey(), n.getId());
        assertEquals(content, "HELLO");
    }

    @Test
    public void get() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final News n = environment.createNews(token.getKey(), news);
        final News n2 = environment.getNews(token.getKey(), n.getId());
        assertEquals(n, n2);
    }

    @Test
    public void list() throws Exception {
        final Token token = newToken("news");
        create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        final List<News> news = environment.news(token.getKey());
        assertEquals(news.size(), 3);
    }

    @Test
    public void hide() throws Exception {
        final Token token = newToken("news");
        create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        final List<News> news = environment.news(token.getKey());
        assertEquals(news.size(), 3);
        final News n = news.get(0);
        environment.hide(token.getKey(), n.getId());
        List<News> news2 = environment.news(token.getKey());
        assertEquals(news2.size(), 2);
    }

    @Test
    public void count() throws Exception {
        final Token token = newToken("news");
        create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        assertEquals((long) environment.newsCount(token.getKey()), 3L);
    }

    @Test
    public void countWithHide() throws Exception {
        final Token token = newToken("news");
        final News news = create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        assertEquals((long) environment.newsCount(token.getKey()), 3L);
        environment.hide(token.getKey(), news.getId());
        assertEquals((long) environment.newsCount(token.getKey()), 2L);
    }

    @Test
    public void publish() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final Long id = environment.createNews(token.getKey(), news).getId();
        final News n1 = environment.getNews(token.getKey(), id);
        assertNull(n1.getPublishedAt());
        environment.publish(token.getKey(), id);
        final News n2 = environment.getNews(token.getKey(), id);
        assertNotNull(n2.getPublishedAt());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void delete() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final Long id = environment.createNews(token.getKey(), news).getId();
        final News n1 = environment.getNews(token.getKey(), id);
        assertNotNull(n1);
        environment.deleteNews(token.getKey(), id);
        environment.getNews(token.getKey(), id);
    }

    @Test
    public void create() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final News n = environment.createNews(token.getKey(), news);
        assertEquals(n.getBref(), news.getBref());
    }

    @Test(expected = ValidationException.class)
    public void createWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final News n = environment.createNews(token.getKey(), news);
        assertEquals(n.getBref(), news.getBref());
    }

    @Test
    public void me() throws Exception {
        final Token token = newToken("news", "profile");
        create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        List<News> news = environment.news(token.getKey());
        assertEquals(news.size(), 3);
        news = environment.meNews(token.getKey());
        assertEquals(news.size(), 3);
    }

    @Test
    public void meWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("news", "profile");
        final List<News> news = environment.meNews(token.getKey());
        assertEquals(news.size(), 0);
    }

    @Test
    public void meCountWithoutProfile() throws Exception {
        final Token token = newTokenWithoutProfile("news", "profile");
        final Long count = environment.meNewsCount(token.getKey());
        assertEquals(count.longValue(), 0L);
    }

    @Test
    public void meCount() throws Exception {
        final Token token = newToken("news", "profile");
        create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        assertEquals((long) environment.meNewsCount(token.getKey()), 3L);
    }

    @Test
    public void meNewsCountWithHide() throws Exception {
        final Token token = newToken("news", "profile");
        final News news = create("TITLE1", "BREF1");
        create("TITLE2", "BREF2");
        create("TITLE3", "BREF3");
        assertEquals((long) environment.meNewsCount(token.getKey()), 3L);
        environment.hide(token.getKey(), news.getId());
        assertEquals((long) environment.meNewsCount(token.getKey()), 2L);
    }

    private News create(String title, String bref) {
        final News news = new News();
        news.setTitle(title);
        news.setBref(bref);
        news.setProfile(successProfile);
        news.setPublishedAt(LocalDateTime.now());
        news.setVisible(true);
        newsRepository.save(news);
        return news;
    }
}