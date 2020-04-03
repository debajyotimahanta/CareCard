package com.coronacarecard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
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
                .and()
                .csrf().disable();

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
    @Override
    public UserDetailsService userDetailsService() {
        //TODO tie this to user table when we support registration
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("umil7qcgPa")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
