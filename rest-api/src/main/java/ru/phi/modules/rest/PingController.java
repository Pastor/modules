package ru.phi.modules.rest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
@Transactional
class PingController {

    @AuthorizedScope(scopes = {"ping"})
    @RequestMapping(value = "/ping/scope", method = RequestMethod.GET)
    public String scope(@AuthorizedToken Token token)
            throws AuthenticationException {
        return "pong";
    }

    @RequestMapping(value = "/ping/clear", method = RequestMethod.GET)
    public String clear()
            throws AuthenticationException {
        return "pong";
    }

//    @RequestMapping(value = "/ping/authorized_fault", method = RequestMethod.GET)
//    public void authorizedFault() {
//    }

    @RequestMapping(value = "/ping/authorized", method = RequestMethod.GET)
    public String authorized(@AuthorizedToken Token token)
            throws AuthenticationException {
        return "pong";
    }
}
