package com.hootch.jwt.tutorial.repository;

import com.hootch.jwt.tutorial.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
}
