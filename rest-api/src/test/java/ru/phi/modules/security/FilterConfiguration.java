package ru.phi.modules.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import ru.phi.modules.api.ExceptionService;

@Configuration
public class FilterConfiguration {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ExceptionService service;

    @Bean(name = "securityFilter")
    public SecurityTokenFilter createFilter() {
        return new SecurityTokenFilter(manager, service);
    }
}
