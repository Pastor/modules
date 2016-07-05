package ru.phi.modules.rest;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;
import ru.phi.modules.security.Scope;

@SuppressWarnings("unused")
@RequestMapping("/rest/v1/")
@RestController
@Transactional
class UserRestController {
    @AuthorizedScope(scope = {Scope.PROFILE})
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    User authorizedUser(@AuthorizedToken Token token)
            throws AuthenticationException {
        return token.getUser();
    }
}
