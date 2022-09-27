package com.hootch.jwt.tutorial.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {


    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_MASTER','ROLE_ADMIN')") //PreAuthorize를 이용해 user과 admin 둘 모두 접속을 허용
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')") //PreAuthorize를 이용해 user과 admin 둘 모두 접속을 허용
    public void getMyUserInfo(HttpServletRequest request) {
//        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
        System.out.println("야호");
    }


}
