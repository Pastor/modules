package ru.phi.modules.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Profile("oauth2")
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class WebSecurityConfiguration  extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("databaseUserDetailsService.v1")
    private UserDetailsService detailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.GET, "/static/index.html")
                .antMatchers(HttpMethod.GET, "/static/bundle.js")
                .antMatchers(HttpMethod.GET, "/static/favicon.ico")
                .antMatchers(HttpMethod.GET, "/static/css/*")
                .antMatchers(HttpMethod.GET, "/static/fonts/*")
                .antMatchers(HttpMethod.GET, "/static/i18n/*")
                .antMatchers(HttpMethod.GET, "/static/images/*")
                .antMatchers(HttpMethod.GET, "/static/scripts/*")

                .antMatchers(HttpMethod.GET, "/api/css/*")
                .antMatchers(HttpMethod.GET, "/api/fonts/*")
                .antMatchers(HttpMethod.GET, "/api/images/*")
                .antMatchers(HttpMethod.GET, "/api/lang/*")
                .antMatchers(HttpMethod.GET, "/api/lib/*")
                .antMatchers(HttpMethod.GET, "/api/specs/*")
                .antMatchers(HttpMethod.GET, "/api/index.html")
                .antMatchers(HttpMethod.GET, "/api/swagger-ui.min.js")

                .antMatchers(HttpMethod.GET, "/rest/v1/ping/clear")
                .antMatchers(HttpMethod.GET, "/rest/v1/accessibility/processes")
                .antMatchers(HttpMethod.GET, "/rest/v1/version")
                .antMatchers(HttpMethod.GET, "/rest/v1/version/swagger")
                .antMatchers(HttpMethod.GET, "/rest/v1/qualities")
                .antMatchers(HttpMethod.GET, "/rest/v1/qualities/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements/{id}/endpoints")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements/count")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories/count")
                .antMatchers(HttpMethod.GET, "/rest/v1/news")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/{id}/content")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/count");
    }
}
