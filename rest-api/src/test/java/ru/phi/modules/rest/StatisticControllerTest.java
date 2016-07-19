package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Statistic;
import ru.phi.modules.entity.Token;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class StatisticControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        createStatistic(54.0000, 55.00000);
        createStatistic(54.1000, 55.10000);
        createStatistic(54.2000, 55.20000);
        createStatistic(54.3000, 55.30000);
        final Token token = newToken("statistic");
        final List<Statistic> statistics = environment.statistics(token.getKey());
        assertEquals(statistics.size(), 4L);
    }

    @Test
    public void createStatistic() throws Exception {
        final Token token = newToken("statistic");
        final Statistic statistic = new Statistic();
        statistic.setLatitude(54.00000);
        statistic.setLongitude(55.00000);
        environment.createStatistic(token.getKey(), statistic);
        assertEquals(statisticRepository.count(), 1L);
    }

    @Test
    public void count() throws Exception {
        createStatistic(54.0000, 55.00000);
        createStatistic(54.1000, 55.10000);
        createStatistic(54.2000, 55.20000);
        createStatistic(54.3000, 55.30000);
        final Token token = newToken("statistic");
        final Long count = environment.statisticsCount(token.getKey());
        assertEquals(count.longValue(), 4L);
    }

    private void createStatistic(double longitude, double latitude) {
        final Statistic statistic = new Statistic();
        statistic.setLatitude(latitude);
        statistic.setLongitude(longitude);
        statistic.setUser(successUser);
        statisticRepository.save(statistic);
    }
}