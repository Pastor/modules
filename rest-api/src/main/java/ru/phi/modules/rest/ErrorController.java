package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Error;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ErrorRepository;
import ru.phi.modules.security.AuthorizedScope;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("errorController.v1")
class ErrorController {

    @Autowired
    private ErrorRepository errorRepository;

    @AuthorizedScope(scopes = {"error"})
    @RequestMapping(value = "/errors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<Error> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                     @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return errorRepository.findAll(pageable).getContent();
    }

    @AuthorizedScope(scopes = {"error"})
    @RequestMapping(value = "/errors/count", method = RequestMethod.GET)
    public long count() {
        return errorRepository.count();
    }
}
