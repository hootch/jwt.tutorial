package com.hootch.jwt.tutorial.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

   private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
   public static final String AUTHORIZATION_HEADER = "Authorization";
   private TokenProvider tokenProvider;

   public JwtFilter(TokenProvider tokenProvider) {
      this.tokenProvider = tokenProvider;
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {  //jwt 토큰의 인증 정보를 현재 실행중인 security context에 저장하는 역할 수행
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      String jwt = resolveToken(httpServletRequest);                                 //리퀘스트에서 토큰을 받아서
      String requestURI = httpServletRequest.getRequestURI();

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {              //토큰 유효성 검증을 돌리고
         Authentication authentication = tokenProvider.getAuthentication(jwt);         //토큰이 정상적이라면 토큰에서 Authentication객체를 받아와서 
         SecurityContextHolder.getContext().setAuthentication(authentication);         //security 컨텍스트에 set해줌
         logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
      } else {
         logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
      }

      filterChain.doFilter(servletRequest, servletResponse);
   }

   private String resolveToken(HttpServletRequest request) {                     //필터링을 위해 Request Header에서 토큰정보를 꺼내오기 위한 메소드
      String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
         return bearerToken.substring(7);
      }

      return null;
   }
}
