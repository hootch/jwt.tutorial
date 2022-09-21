package com.hootch.jwt.tutorial.repository;

import  com.hootch.jwt.tutorial.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
