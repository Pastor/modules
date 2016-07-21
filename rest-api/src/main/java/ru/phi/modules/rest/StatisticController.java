package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Statistic;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.StatisticRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("staticController.v1")
class StatisticController extends AbstractController {

    @Autowired
    private StatisticRepository statisticRepository;

    @AuthorizedScope(scopes = {"statistic"})
    @RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Statistic> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                         @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return statisticRepository.findAll(pageable).getContent();
    }

    @Transactional
    @AuthorizedScope(scopes = {"statistic"})
    @RequestMapping(value = "/statistics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Statistic createStatistic(@AuthorizedToken Token token,
                              @RequestBody Statistic statistic)
            throws AuthenticationException {
        statistic.clear();
        statistic.setUser(token.getUser());
        statistic.setPoint(point(token.getUser(), statistic.getPoint()));
        statisticRepository.save(statistic);
        return statistic;
    }

    @AuthorizedScope(scopes = {"statistic"})
    @RequestMapping(value = "/statistics/count", method = RequestMethod.GET)
    public long count() {
        return statisticRepository.count();
    }
}
