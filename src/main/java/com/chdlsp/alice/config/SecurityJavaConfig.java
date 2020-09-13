package com.chdlsp.alice.config;

import com.chdlsp.alice.interfaces.filter.JwtAuthenticationFilter;
import com.chdlsp.alice.interfaces.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Filter filter = new JwtAuthenticationFilter(
                authenticationManager(), jwtUtil());

        http
                // api server 에서 불필요한 options disable 처리
                .formLogin().disable()
                .csrf().disable()
                .cors().disable()
                .headers().frameOptions().disable()
                .and()
                // filter 처리
                .addFilter(filter)
                // session 처리 - stateless 방식, 계속해서 토큰 검증 수행
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    // IoC 주입
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret);
    }
}
