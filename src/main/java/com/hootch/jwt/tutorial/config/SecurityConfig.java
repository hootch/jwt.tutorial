package com.hootch.jwt.tutorial.config;

import com.hootch.jwt.tutorial.jwt.JwtAccessDeniedHandler;
import com.hootch.jwt.tutorial.jwt.JwtAuthenticationEntryPoint;
import com.hootch.jwt.tutorial.jwt.JwtSecurityConfig;
import com.hootch.jwt.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = false, prePostEnabled = true, jsr250Enabled = false)
/*
    1.securedEnabled
    @Secured 애노테이션을 사용하여 인가 처리를 하고 싶을때 사용하는 옵션이다.
    기본값은 false

    2.prePostEnabled
    @PreAuthorize, @PostAuthorize 애노테이션을 사용하여 인가 처리를 하고 싶을때 사용하는 옵션이다.
    기본값은 false
    
    3.jsr250Enabled
    @RolesAllowed 애노테이션을 사용하여 인가 처리를 하고 싶을때 사용하는 옵션이다.
    기본값은 false
 */
public class SecurityConfig {           
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {                                  
        return (web) -> web.ignoring().antMatchers("/h2-console/**"        
                , "/favicon.ico"                                                           //파비콘 요청은 spring securityy로직을 수행하지 않고 접근할 수 있게 설정해주는거임....  
                , "/error");
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                    .csrf().disable()                                       // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                    
                    .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                    .exceptionHandling()                                    //exception을 처리할때
                    
                    //우리가 만들었던 클래스들을 추가해줌
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)  //유효한 자격증명을 제공하지 않고 접근하려고 할때 401 에러를 리턴하는 클래스임
                    .accessDeniedHandler(jwtAccessDeniedHandler)

                    // enable h2-console
                    .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin()

                    // 세션을 사용하지 않기 때문에 STATELESS로 설정
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    
                    .and()
                    .authorizeRequests()   //httpServletRequest를 사용하는 요청들에 대한 접근제한을 설정
                    .antMatchers("/api/hello").permitAll()          // "/api/hello"에 대한 요청은 인증없이 접근을 허용하겠다라는 의미
                    .antMatchers("/api/authenticate").permitAll()   //토큰을 받기위한 로그인과
                    .antMatchers("/api/signup").permitAll()         //회원가입을 위한 api는 접근을 허용
                    .antMatchers("/api/main").permitAll()         //회원가입을 위한 api는 접근을 허용
                    .antMatchers("/login").permitAll()         //회원가입을 위한 api는 접근을 허용

                    .anyRequest().authenticated()                           //나머지 요청들에 대해선 모두 인증되어야 한다는 의미
                    

                    .and()
                    .apply(new JwtSecurityConfig(tokenProvider));//jwt필터를 addFilterBeofre로 등록했던 jwtSecurityConfig클래스도 적용을 해줌????뭔개소리야 이건
        
        return httpSecurity.build(); 
    }



}
