package com.nashtech.assignment.config;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.filters.AuthenticationFilter;
import com.nashtech.assignment.filters.ExceptionHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            AuthenticationFilter authenticationFilter,
            ExceptionHandlerFilter exceptionHandlerFilter) throws Exception {
        httpSecurity.csrf().disable().cors();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.authorizeHttpRequests()
                .antMatchers("/api/user/change-password/**", "/api/assignment/user/**")
                .hasAnyAuthority(EUserType.ADMIN.toString(), EUserType.STAFF.toString())
                .antMatchers(HttpMethod.POST, "/api/return-asset")
                .hasAnyAuthority(EUserType.ADMIN.toString(), EUserType.STAFF.toString())
                .antMatchers(HttpMethod.GET, "/api/user/{username:[a-z]+}/current")
                .hasAnyAuthority(EUserType.ADMIN.toString(), EUserType.STAFF.toString())
                .anyRequest().hasAuthority(EUserType.ADMIN.toString());
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(exceptionHandlerFilter, AuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/login");
    }
}
