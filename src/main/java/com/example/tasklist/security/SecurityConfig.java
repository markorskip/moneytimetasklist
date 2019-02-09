package com.example.tasklist.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(encoder());

    }

    @Bean
    public PasswordEncoder encoder() {
        return new StandardPasswordEncoder("secret");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/main")
                    .access("hasRole('ROLE_USER')")
                .antMatchers("/", "/**","/tasklist/*")
                    .access("permitAll")
                .and()
                    .formLogin()
                    .loginPage("/login")
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                .and()
                    .csrf()
                    .ignoringAntMatchers("/h2-console/**","/tasklist/**")
                .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin()
        ;
    }


}
