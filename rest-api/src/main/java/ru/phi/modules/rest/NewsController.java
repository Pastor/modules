package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.News;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.NewsRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
@Transactional
class NewsController {

    @Autowired
    private NewsRepository newsRepository;


    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<News> news(@AuthorizedToken Token token,
                    @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                    @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return newsRepository.findAll(pageable).getContent();
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    News createNews(@AuthorizedToken Token token,
                    @RequestBody News news)
            throws AuthenticationException {
        news.clear();
        news.setUser(token.getUser());
        newsRepository.save(news);
        return news;
    }
}
