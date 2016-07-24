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
import ru.phi.modules.entity.Quality;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.QualityRepository;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("qualityController.v1")
class QualityController {

    @Autowired
    private QualityRepository qualityRepository;

    @RequestMapping(value = "/qualities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Quality> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return qualityRepository.findAll(pageable).getContent();
    }

    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Quality get(@PathVariable("id") Long id) {
        final Quality one = qualityRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one;
    }

    @PreAuthorize("#oauth2.hasScope('write:quality') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody Quality quality) {
        final Quality one = qualityRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setName(quality.getName());
        one.setAccessibility(quality.getAccessibility());
        one.setTemplate(quality.getTemplate());
        qualityRepository.save(one);
    }

    @PreAuthorize("#oauth2.hasScope('delete:quality') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/qualities/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        if (!qualityRepository.exists(id))
            throw new ObjectNotFoundException(id);
        qualityRepository.delete(id);
    }

    @Transactional
    @PreAuthorize("#oauth2.hasScope('write:quality') and hasAnyRole('ROLE_admin')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/qualities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Quality create(@AuthenticationPrincipal UserGetter getter,
                   @RequestBody Quality quality) {
        quality.clear();
        quality.setUser(getter.user());
        qualityRepository.save(quality);
        return quality;
    }
}
