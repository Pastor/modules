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
import ru.phi.modules.entity.ElementCategory;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.exceptions.ObjectNotFoundException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.ElementCategoryRepository;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("elementCategoryController.v1")
class ElementCategoryController {

    @Autowired
    private ElementCategoryRepository elementCategoryRepository;

    @RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    List<ElementCategory> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                               @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return elementCategoryRepository.findAll(pageable).getContent();
    }

    @Transactional
    @PreAuthorize("#oauth2.hasScope('write:category') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/categories", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public
    @ResponseBody
    ElementCategory create(@AuthenticationPrincipal UserGetter getter,
                           @RequestBody ElementCategory category)
            throws AuthenticationException {
        category.clear();
        category.setUser(getter.user());
        elementCategoryRepository.save(category);
        return category;
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ElementCategory get(@PathVariable("id") Long id)
            throws AuthenticationException {
        final ElementCategory one = elementCategoryRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        return one;
    }

    @PreAuthorize("#oauth2.hasScope('write:category') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody ElementCategory category)
            throws AuthenticationException {
        final ElementCategory one = elementCategoryRepository.findOne(id);
        if (one == null)
            throw new ObjectNotFoundException(id);
        one.setName(category.getName());
        one.setIcon(category.getIcon());
        elementCategoryRepository.save(one);
    }

    @PreAuthorize("#oauth2.hasScope('delete:category') and hasAnyRole('ROLE_admin')")
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        if (!elementCategoryRepository.exists(id))
            throw new ObjectNotFoundException(id);
        elementCategoryRepository.delete(id);
    }

    @RequestMapping(value = "/categories/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public long count() {
        return elementCategoryRepository.count();
    }
}
