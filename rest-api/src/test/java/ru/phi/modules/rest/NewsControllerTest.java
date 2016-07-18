package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.ObjectNotFoundException;

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

    @Test
    public void delete() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final Long id = environment.createNews(token.getKey(), news).getId();
        final News n1 = environment.getNews(token.getKey(), id);
        assertNotNull(n1);
        environment.delete(token.getKey(), id);
        final News n2 = environment.getNews(token.getKey(), id);
        assertNull(n2);
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

    private News create(String title, String bref) {
        final News news = new News();
        news.setTitle(title);
        news.setBref(bref);
        news.setProfile(successProfile);
        newsRepository.save(news);
        return news;
    }
}