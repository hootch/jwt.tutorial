package com.hootch.jwt.tutorial.service;

import java.util.Collections;
import java.util.Optional;
import com.hootch.jwt.tutorial.dto.UserDto;
import com.hootch.jwt.tutorial.entity.Authority;
import com.hootch.jwt.tutorial.entity.User;
import com.hootch.jwt.tutorial.exception.DuplicateMemberException;
import com.hootch.jwt.tutorial.exception.NotFoundMemberException;
import com.hootch.jwt.tutorial.repository.UserRepository;
import com.hootch.jwt.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {//유저네임을 파라미터로 받아서 어떠한유저네임이든 유저네임에 해당하는 유저객체와 권한정보를 가져오는 메소드
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {     //현재 시큐리티컨텍스트에 저장이 되어 있는 username에 해당하는 유저정보와 권한정보만 받을 수 있다고 하네요
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
