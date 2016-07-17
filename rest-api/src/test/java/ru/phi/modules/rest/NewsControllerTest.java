package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Token;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class NewsControllerTest extends AbstractRestTest {

    @Test
    public void news() throws Exception {
        final Token token = newToken("news");
        createNews("TITLE1", "BREF1");
        createNews("TITLE2", "BREF2");
        createNews("TITLE3", "BREF3");
        final List<News> news = environment.news(token.getKey());
        assertEquals(news.size(), 3);
    }

    @Test
    public void createNews() throws Exception {
        final Token token = newToken("news");
        final News news = new News();
        news.setTitle("B");
        news.setBref("P");
        final News n = environment.createNews(token.getKey(), news);
        assertEquals(n.getBref(), news.getBref());
    }

    private void createNews(String title, String bref) {
        final News news = new News();
        news.setTitle(title);
        news.setBref(bref);
        news.setUser(successUser);
        newsRepository.save(news);
    }
}