package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Statistic;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.StatisticRepository;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("staticController.v1")
class StatisticController extends AbstractController {

    @Autowired
    private StatisticRepository statisticRepository;

    @PreAuthorize("#oauth2.hasScope('read:statistic') and hasAnyRole('ROLE_admin')")
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
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/statistics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Statistic createStatistic(@AuthenticationPrincipal UserGetter getter,
                              @RequestBody Statistic statistic)
            throws AuthenticationException {
        statistic.clear();
        statistic.setUser(getter.user());
        statistic.setPoint(point(getter.user(), statistic.getPoint()));
        statisticRepository.save(statistic);
        return statistic;
    }

    @PreAuthorize("#oauth2.hasScope('read:statistic') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/statistics/count", method = RequestMethod.GET)
    public long count() {
        return statisticRepository.count();
    }
}
