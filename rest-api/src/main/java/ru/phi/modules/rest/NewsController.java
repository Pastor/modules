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
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;
import ru.phi.modules.repository.NewsRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
@Transactional
class NewsController {

    @Autowired
    private NewsRepository newsRepository;


    @RequestMapping(value = "/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<News> news(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                    @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return newsRepository.list(pageable).getContent();
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    News get(@PathVariable("id") Long id)
            throws AuthenticationException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one;
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news/{id}", method = RequestMethod.PUT)
    public void put(@PathVariable("id") Long id, @RequestBody News news)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setBref(news.getBref());
        one.setTitle(news.getTitle());
        newsRepository.save(one);
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        newsRepository.delete(id);
    }

    @RequestMapping(value = "/news/{id}/content", method = RequestMethod.GET)
    public String getContent(@PathVariable("id") Long id)
            throws AuthenticationException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one.getContent();
    }

    @RequestMapping(value = "/news/count", method = RequestMethod.GET)
    public long count() {
        return newsRepository.listCount();
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news/{id}/content", method = RequestMethod.PUT)
    public void putContent(@PathVariable("id") Long id, @RequestBody String content)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setContent(content);
        newsRepository.save(one);
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news/{id}/publish", method = RequestMethod.PUT)
    public void publish(@PathVariable("id") Long id)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setPublishedAt(LocalDateTime.now());
        one.setVisible(true);
        newsRepository.save(one);
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news/{id}/hide", method = RequestMethod.PUT)
    public void hide(@PathVariable("id") Long id)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setVisible(false);
        newsRepository.save(one);
    }

    @AuthorizedScope(scopes = {"news"})
    @RequestMapping(value = "/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    News createNews(@AuthorizedToken Token token,
                    @RequestBody News news) {
        news.clear();
        if (token.getUser().getProfile() != null) {
            news.setProfile(token.getUser().getProfile());
        } else {
            throw new ValidationException("Только пользователь с профилем может создавать новости");
        }
        newsRepository.save(news);
        return news;
    }
}
