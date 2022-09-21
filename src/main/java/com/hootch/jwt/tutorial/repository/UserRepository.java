package com.hootch.jwt.tutorial.repository;



import com.hootch.jwt.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
   @EntityGraph(attributePaths = "authorities")                      //entitygraph는 earger조회로 authorities정보를 같이 가져옴
   Optional<User> findOneWithAuthoritiesByUsername(String username);
}
