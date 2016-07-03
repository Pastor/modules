package ru.phi.modules.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

@Configuration
public class FilterConfiguration {
    @Autowired
    private AuthenticationManager manager;

    @Bean(name = "securityFilter")
    public SecurityTokenFilter createFilter() {
        return new SecurityTokenFilter(manager);
    }
}
