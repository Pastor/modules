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

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
class SecurityRestController {

    @Autowired
    private AuthenticateService service;

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Token update(@RequestParam(name = "scopes", required = false) String scopes, WebRequest request, HttpServletRequest servletRequest)
            throws AuthenticationException, UnsupportedEncodingException {
        final Basic basic = Basic.parse(request);
        final String method = servletRequest.getMethod();
        if (scopes == null || scopes.isEmpty()) {
            return service.authenticate(basic.username, basic.password, RequestMethod.PUT.name().equalsIgnoreCase(method));
        } else {
            return service.authenticate(basic.username, basic.password, RequestMethod.PUT.name().equalsIgnoreCase(method),
                    Sets.newHashSet(scopes.split(",")));
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
