package com.user.api_challenge.user.dao;

import com.user.api_challenge.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findUserByEmail(String email);
  User findUserByUserName(String username);
  User findUserById(Long id);
}
