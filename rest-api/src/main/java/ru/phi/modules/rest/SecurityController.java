package ru.phi.modules.rest;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.security.AuthorizedToken;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("securityController.v1")
class SecurityController {

    @Autowired
    private AuthenticateService service;

    @RequestMapping(value = "/token", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Token create(@RequestParam(name = "scopes", required = false) String scopes,
                 @RequestParam(name = "type", required = false) String type,
                 WebRequest request)
            throws AuthenticationException, UnsupportedEncodingException {
        final Basic basic = Basic.parse(request);
        if (scopes.isEmpty()) {
            return service.authenticate(basic.username, basic.password, null);
        } else {
            return service.authenticate(basic.username, basic.password, Sets.newHashSet(scopes.split(",")));
        }
    }

    @RequestMapping(value = "/token", method = {RequestMethod.PUT},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    void update(@AuthorizedToken Token token, @RequestParam(name = "scopes", required = false) String scopes, WebRequest request)
            throws AuthenticationException, UnsupportedEncodingException {

        if (scopes.isEmpty()) {
            service.updateToken(token, null);
        } else {
            service.updateToken(token, Sets.newHashSet(scopes.split(",")));
        }
    }

    private static class Basic {
        private final String username;
        private final String password;

        private Basic(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public static Basic parse(WebRequest request) throws UnsupportedEncodingException {
            final String header = request.getHeader("Authorization");
            if (header == null)
                throw new BadCredentialsException(
                        "Empty basic authentication token");
            final byte[] base64Token = header.substring(6).getBytes("UTF-8");
            final byte[] decoded;
            try {
                decoded = Base64.decode(base64Token);
            } catch (IllegalArgumentException e) {
                throw new BadCredentialsException(
                        "Failed to decode basic authentication token");
            }
            final String token = new String(decoded, "UTF-8");
            final int delim = token.indexOf(":");
            if (delim == -1) {
                throw new BadCredentialsException("Invalid basic authentication token");
            }
            return new Basic(token.substring(0, delim), token.substring(delim + 1));
        }
    }
}
