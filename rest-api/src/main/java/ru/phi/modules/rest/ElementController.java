package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Element;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ElementRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
class ElementController {

    @Autowired
    private ElementRepository elementRepository;

    @RequestMapping(value = "/elements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Element> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return elementRepository.findAll(pageable).getContent();
    }

    @RequestMapping(value = "/elements/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Element get(@PathVariable("id") Long id)
            throws AuthenticationException {
        return elementRepository.findOne(id);
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.PUT)
    public void put(@PathVariable("id") Long id, @RequestBody Element element)
            throws AuthenticationException {
        final Element one = elementRepository.findOne(id);
        one.setAddress(element.getAddress());
        one.setFullName(element.getFullName());
        one.setName(element.getName());
        one.setInfo(element.getInfo());
        one.setLatitude(element.getLatitude());
        one.setLongitude(element.getLongitude());
        elementRepository.save(one);
    }

    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void delete(@PathVariable("id") Long id)
            throws AuthenticationException {
        elementRepository.delete(id);
    }

    @Transactional
    @AuthorizedScope(scopes = {"element"})
    @RequestMapping(value = "/elements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Element createElement(@AuthorizedToken Token token,
                          @RequestBody Element element)
            throws AuthenticationException {
        element.clear();
        element.setUser(token.getUser());
        elementRepository.save(element);
        return element;
    }

}
