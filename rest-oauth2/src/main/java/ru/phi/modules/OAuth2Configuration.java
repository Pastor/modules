package ru.phi.modules;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = {"ru.phi.modules.oauth2"})
@Import({
        JpaConfiguration.class
})
@Profile("oauth2")
public class OAuth2Configuration {
}
