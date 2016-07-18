package ru.phi.modules;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"ru.phi.modules.rest", "ru.phi.modules.exceptions"})
public class RestMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    @Qualifier("authorizedToken.v1")
    private HandlerMethodArgumentResolver resolver;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/rest/v1/version/swagger");
        registry.addMapping("/rest/v1/version");
        registry.addMapping("/rest/v1/elements").allowedMethods("POST", "GET");
        registry.addMapping("/rest/v1/me").allowedMethods("PUT", "GET");
        registry.addMapping("/rest/v1/me/settings").allowedMethods("PUT", "GET");
        registry.addMapping("/rest/v1/me/news").allowedMethods("POST", "GET");
        registry.addMapping("/rest/v1/qualities").allowedMethods("POST", "GET");
        registry.addMapping("/rest/v1/qualities/{id}").allowedMethods("PUT", "GET", "DELETE");
        registry.addMapping("/rest/v1/news").allowedMethods("POST", "GET");
        registry.addMapping("/rest/v1/news/{id}").allowedMethods("PUT", "GET", "DELETE");
        registry.addMapping("/rest/v1/news/{id}/publish").allowedMethods("PUT");
        registry.addMapping("/rest/v1/news/{id}/content").allowedMethods("PUT", "GET");
        registry.addMapping("/rest/v1/categories").allowedMethods("POST", "GET");
        registry.addMapping("/rest/v1/categories/{id}").allowedMethods("PUT", "GET", "DELETE");
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }
}
