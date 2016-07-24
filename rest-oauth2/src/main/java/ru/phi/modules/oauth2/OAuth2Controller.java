package ru.phi.modules.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.entity.User;
import ru.phi.modules.repository.UserRepository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Profile("oauth2")
@RestController("oauth2Controller")
@Service
public class OAuth2Controller {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    @Qualifier("userRepository.v1")
    private UserRepository userRepository;

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@AuthenticationPrincipal UserGetter user) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, user.user().getUsername()));
    }

    @PreAuthorize("hasRole('ROLE_admin') and #oauth2.hasScope('read:user')")
    @RequestMapping("/users")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_content') and #oauth2.hasScope('read:user')")
    @RequestMapping("/contents")
    public String getContent() {
        return "";
    }


    @PreAuthorize("#oauth2.hasScope('read:user')")
    @RequestMapping(path = "/me", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void me(OAuth2Authentication auth) {
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        final OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        System.out.println(accessToken);
        final Map<String, Object> information = accessToken.getAdditionalInformation();
        System.out.println(information);
    }

    @SuppressWarnings("unused")
    private static final class Greeting {
        private final long id;
        private final String content;

        public long getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        Greeting(long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

}
