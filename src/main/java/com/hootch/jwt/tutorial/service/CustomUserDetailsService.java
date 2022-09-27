package com.hootch.jwt.tutorial.service;

import com.hootch.jwt.tutorial.entity.User;
import com.hootch.jwt.tutorial.entity.UserAuth;
import com.hootch.jwt.tutorial.repository.UserAuthRepository;
import com.hootch.jwt.tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
   private final UserRepository userRepository;
   private final UserAuthRepository userAuthRepository;


   @Override
   @Transactional
   public UserDetails loadUserByUsername(final String username) { //AuthController의 authorize 메소드에서 실행함
      UserAuth auths = userAuthRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username + "데이터베이스에서 찾을 수 없습니다"));
      return userRepository.findOneWithAuthoritiesByUserid(username)  //로그인 시 db에서 유저정보와 권한정보를 가져와서
         .map(user -> createUser(username, user, auths))                       //그 정보를 기반으로  userdetails.User 객체를 생성함
         .orElseThrow(() -> new UsernameNotFoundException(username    + " -> 데이터베이스에서 찾을 수 없습니다."));
//      return userRepository.findOneWithAuthoritiesByUserid(username)  //로그인 시 db에서 유저정보와 권한정보를 가져와서
//         .map(user -> createUser(username, user))                       //그 정보를 기반으로  userdetails.User 객체를 생성함
//         .orElseThrow(() -> new UsernameNotFoundException(username    + " -> 데이터베이스에서 찾을 수 없습니다."));
   }

   private org.springframework.security.core.userdetails.User createUser(String username, User user, UserAuth userAuth) {
      if (!user.isActivated()) {
         throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
      }

//      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
//              .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
//              .collect(Collectors.toList());
      List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
      grantedAuthorities.add( new SimpleGrantedAuthority(userAuth.getAuth1()));
      grantedAuthorities.add( new SimpleGrantedAuthority(userAuth.getAuth2()));
//      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
//              .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
//              .collect(Collectors.toList());

      return new org.springframework.security.core.userdetails.User(user.getUserid(),
              user.getPassword(),
              grantedAuthorities);
   }
}
