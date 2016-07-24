package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.News;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.exceptions.ValidationException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.NewsRepository;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@Transactional
@RestController("newsController.v1")
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

    @PreAuthorize("#oauth2.hasScope('update:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
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

    @PreAuthorize("#oauth2.hasScope('delete:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
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

    @PreAuthorize("#oauth2.hasScope('write:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
    @RequestMapping(value = "/news/{id}/content", method = RequestMethod.PUT)
    public void putContent(@PathVariable("id") Long id, @RequestBody String content)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setContent(content);
        newsRepository.save(one);
    }

    @PreAuthorize("#oauth2.hasScope('write:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
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

    @PreAuthorize("#oauth2.hasScope('write:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
    @RequestMapping(value = "/news/{id}/hide", method = RequestMethod.PUT)
    public void hide(@PathVariable("id") Long id)
            throws AuthenticationException, ObjectNotFoundException {
        final News one = newsRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setVisible(false);
        newsRepository.save(one);
    }

    @PreAuthorize("#oauth2.hasScope('write:content') and hasAnyRole('ROLE_admin', 'ROLE_content')")
    @RequestMapping(value = "/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    News createNews(@AuthenticationPrincipal UserGetter getter,
                    @RequestBody News news) {
        news.clear();
        if (getter.user().getProfile() != null) {
            news.setProfile(getter.user().getProfile());
        } else {
            throw new ValidationException("Только пользователь с профилем может создавать новости");
        }
        newsRepository.save(news);
        return news;
    }
}
