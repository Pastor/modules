package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.ElementCategory;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ElementCategoryRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
class ElementCategoryController {

    @Autowired
    private ElementCategoryRepository elementCategoryRepository;

    @RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @AuthorizedScope(scopes = {"categories"})
    @RequestMapping(value = "/categories", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    ElementCategory createCategory(@AuthorizedToken Token token,
                                   @RequestBody ElementCategory category)
            throws AuthenticationException {
        category.clear();
        category.setUser(token.getUser());
        elementCategoryRepository.save(category);
        return category;
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    ElementCategory get(@PathVariable("id") Long id)
            throws AuthenticationException {
        return elementCategoryRepository.findOne(id);
    }

    @AuthorizedScope(scopes = {"categories"})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public void put(@PathVariable("id") Long id, @RequestBody ElementCategory category)
            throws AuthenticationException {
        final ElementCategory one = elementCategoryRepository.findOne(id);
        one.setName(category.getName());
        one.setIcon(category.getIcon());
        elementCategoryRepository.save(one);
    }

    @AuthorizedScope(scopes = {"categories"})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        elementCategoryRepository.delete(id);
    }
}
