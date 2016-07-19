package ru.phi.modules.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.phi.modules.api.ExceptionService;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan({"ru.phi.modules.security"})
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenAuthenticateProvider provider;

    @Autowired
    private RestAuthenticationEntryPoint entryPoint;

    @Autowired
    private ExceptionService service;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/resources/*")
                .antMatchers(HttpMethod.POST, "/rest/v1/token")
                .antMatchers(HttpMethod.GET, "/rest/v1/ping/clear")
                .antMatchers(HttpMethod.GET, "/rest/v1/version")
                .antMatchers(HttpMethod.GET, "/rest/v1/version/swagger")
                .antMatchers(HttpMethod.GET, "/rest/v1/qualities")
                .antMatchers(HttpMethod.GET, "/rest/v1/qualities/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/elements/count")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/categories/count")
                .antMatchers(HttpMethod.GET, "/rest/v1/news")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/{id}")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/{id}/content")
                .antMatchers(HttpMethod.GET, "/rest/v1/news/count")
                .antMatchers(HttpMethod.GET, "/rest/v1/token");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().authorizeRequests()
                .anyRequest().authenticated().
                and()
                .anonymous().disable()
                .securityContext()
                .and()
                .headers().disable()
                .rememberMe().disable()
                .requestCache().disable()
                .x509().disable()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilterBefore(new SecurityTokenFilter(authenticationManager(), service), BasicAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(entryPoint);
    }

//    @Bean
//    public FilterRegistrationBean authFilter(@Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilte) throws Exception {
//        final FilterRegistrationBean registrationBean = new FilterRegistrationBean(new SecurityTokenFilter(authenticationManager()));
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        registrationBean.setName(SecurityTokenFilter.class.getName());
//        registrationBean.setEnabled(true);
//        registrationBean.addUrlPatterns("/rest/*");
//        return registrationBean;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
