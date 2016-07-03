package ru.phi.modules.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.security.AuthorizedUser;

@SuppressWarnings("unused")
@RequestMapping("/rest/v1/")
@RestController
final class UserRestController {
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    User authorizedUser(@AuthorizedUser User user)
            throws AuthenticationException {
        return user;
    }
}
