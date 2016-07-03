package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

@RequestMapping("/rest/v1/")
@RestController
final class SecurityRestController {

    @Autowired
    private AuthenticateService service;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Token doLogin(@RequestParam(name = "login", required = false) String login,
                  @RequestParam(name = "password", required = false) String password)
            throws AuthenticationException {
        return service.authenticate(login, password);
    }
}
