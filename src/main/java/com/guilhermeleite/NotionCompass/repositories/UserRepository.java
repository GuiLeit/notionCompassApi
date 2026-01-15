package com.guilhermeleite.NotionCompass.repositories;

import com.guilhermeleite.NotionCompass.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNotionUserId(String notionUserId);
    UserDetails findUserDetailsByNotionUserId(String notionUserId);
}
