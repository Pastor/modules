package ru.phi.modules.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.oauth2.UserGetter;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@Transactional
@RestController("pingController.v1")
class PingController {

    @PreAuthorize("#oauth2.hasScope('read:ping')")
    @RequestMapping(value = "/ping/scope", method = RequestMethod.GET)
    public String scope(@AuthenticationPrincipal UserGetter getter) {
        return "pong";
    }

    @RequestMapping(value = "/ping/clear", method = RequestMethod.GET)
    public String clear()
            throws AuthenticationException {
        return "pong";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/ping/authorized", method = RequestMethod.GET)
    public String authorized() {
        return "pong";
    }
}
