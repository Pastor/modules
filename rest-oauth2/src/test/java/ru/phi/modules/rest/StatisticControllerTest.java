package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import ru.phi.modules.entity.Statistic;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class StatisticControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        createStatistic(54.0000, 55.00000);
        createStatistic(54.1000, 55.10000);
        createStatistic(54.2000, 55.20000);
        createStatistic(54.3000, 55.30000);
        final String accessToken = newToken("read:statistic");
        get("/rest/v1/statistics", accessToken)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void createStatistic() throws Exception {
        final String accessToken = newToken("empty");
        final Statistic statistic = new Statistic();
        statistic.setPoint(point(successUser, 54.00000, 55.000000));
        create("/rest/v1/statistics", accessToken, statistic).andExpect(status().isCreated());
        assertEquals(statisticRepository.count(), 1L);
    }

    @Test
    public void count() throws Exception {
        createStatistic(54.0000, 55.00000);
        createStatistic(54.1000, 55.10000);
        createStatistic(54.2000, 55.20000);
        createStatistic(54.3000, 55.30000);
        final String accessToken = newToken("read:statistic");
        final String content = getContent("/rest/v1/statistics/count", accessToken)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(Long.parseLong(content), 4L);
    }

    private void createStatistic(double longitude, double latitude) {
        final Statistic statistic = new Statistic();
        statistic.setPoint(point(successUser, latitude, longitude));
        statistic.setUser(successUser);
        statisticRepository.save(statistic);
    }
}