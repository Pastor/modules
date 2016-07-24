package ru.phi.modules.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import static ru.phi.modules.Constants.RESOURCE_ID;

@Profile("oauth2")
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    @Qualifier("oauth2TokenService.v1")
    private ResourceServerTokenServices services;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // @formatter:off
        resources
                .resourceId(RESOURCE_ID)
                .tokenServices(services);
        // @formatter:on
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
                .authorizeRequests().anyRequest().authenticated();
        // @formatter:on
    }

//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
    // @formatter:off
//        http
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and().authorizeRequests().anyRequest().authenticated();
//            .requestMatchers().antMatchers("/foos/**","/bars/**")
//            .and()
//            .authorizeRequests()
//                .antMatchers(HttpMethod.GET,"/foos/**").access("#oauth2.hasScope('foo') and #oauth2.hasScope('read')")
//                .antMatchers(HttpMethod.POST,"/foos/**").access("#oauth2.hasScope('foo') and #oauth2.hasScope('write')")
//                .antMatchers(HttpMethod.GET,"/bars/**").access("#oauth2.hasScope('bar') and #oauth2.hasScope('read')")
//                .antMatchers(HttpMethod.POST,"/bars/**").access("#oauth2.hasScope('bar') and #oauth2.hasScope('write') and hasRole('ROLE_ADMIN')")
    ;
    // @formatter:on
//    }
}
