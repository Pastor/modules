package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.Token;
import ru.phi.modules.exceptions.AuthenticationException;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("unused")
@RequestMapping("/rest/v1/")
@RestController
class SecurityRestController {

    @Autowired
    private AuthenticateService service;

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Token update(WebRequest request)
            throws AuthenticationException, UnsupportedEncodingException {
        String header = request.getHeader("Authorization");
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
        final String username = token.substring(0, delim);
        final String password = token.substring(delim + 1);
        return service.authenticate(username, password);
    }
}
