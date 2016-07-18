package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Quality;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.QualityRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
class QualityController {

    @Autowired
    private QualityRepository qualityRepository;

    @RequestMapping(value = "/qualities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Quality> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return qualityRepository.findAll(pageable).getContent();
    }

    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Quality get(@PathVariable("id") Long id)
            throws AuthenticationException {
        return qualityRepository.findOne(id);
    }

    @AuthorizedScope(scopes = {"quality"})
    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.PUT)
    public void put(@PathVariable("id") Long id, @RequestBody Quality quality)
            throws AuthenticationException {
        final Quality one = qualityRepository.findOne(id);
        one.setName(quality.getName());
        one.setAccessibility(quality.getAccessibility());
        one.setTemplate(quality.getTemplate());
        qualityRepository.save(one);
    }

    @AuthorizedScope(scopes = {"quality"})
    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        qualityRepository.delete(id);
    }

    @Transactional
    @AuthorizedScope(scopes = {"quality"})
    @RequestMapping(value = "/qualities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Quality create(@AuthorizedToken Token token,
                   @RequestBody Quality quality)
            throws AuthenticationException {
        quality.clear();
        quality.setUser(token.getUser());
        qualityRepository.save(quality);
        return quality;
    }
}
