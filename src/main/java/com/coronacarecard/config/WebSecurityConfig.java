package com.coronacarecard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${spring.h2.console.enabled}")
    private boolean isH2ConsoleEnabled;

    @Value("${spring.app.forntEndBaseUrl}")
    private String forntEndBaseUrl;


    private static final String[] AUTH_WHITELIST = {

            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/admin/**").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll()
                .and().cors()
                //TODO Maybe we need this enabled. But out does front end get the CSRF token
                .and().csrf().disable();
        if(isH2ConsoleEnabled) {
            http.headers().frameOptions().sameOrigin();
        }
        // TODO uncomment this make it strcit once we have login
                /*
                .antMatchers("/**").permitAll()
                .antMatchers("/business/*", "/owner/claim").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll();

                 */

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        //TODO (deba) remove this once we have the correct envs
        configuration.setAllowedOrigins(asList(forntEndBaseUrl, "https://www.beta.coronacarecard.com"
                , "https://www.coronacarecard.com"));
        configuration.setAllowedMethods(asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        //TODO tie this to user table when we support registration
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("umil7qcgPa")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
