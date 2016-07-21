package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.Utilities;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ScopeRepository;
import ru.phi.modules.security.AuthorizedToken;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("scopeController.v1")
class ScopesController {
    @Autowired
    private ScopeRepository scopeRepository;

    @RequestMapping(value = "/scopes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<String> list(@AuthorizedToken Token token)
            throws AuthenticationException {
        return scopeRepository.findByRole(token.getUser().getRole()).stream().map(Scope::getName)
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void construct() {
        Utilities.register(scopeRepository);
    }
}
