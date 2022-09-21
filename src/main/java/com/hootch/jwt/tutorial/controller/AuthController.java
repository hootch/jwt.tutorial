package com.hootch.jwt.tutorial.controller;

import com.hootch.jwt.tutorial.dto.LoginDto;
import com.hootch.jwt.tutorial.dto.TokenDto;
import com.hootch.jwt.tutorial.jwt.JwtFilter;
import com.hootch.jwt.tutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //어센티케이션 토큰을 이용하여 어센티케이트가 실행이 될 때 customsuerdetailsService에 있는 loadUserByUsername이 실행됨
        //그 후 그 결과값을 가지고 authentication 객체를 생성함
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //그 객체를 SecurityContext 객체에 저장 하고

        String jwt = tokenProvider.createToken(authentication);
        // authentication객체를 createToken메소드를 통해 JWT Token을 생성함

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        //그 JWT Token을 Response Head에도 넣어주고,

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
        //토큰 DTO를 통해 ResponseBody에도 넣어서 리턴하게 됨.
    }
}
