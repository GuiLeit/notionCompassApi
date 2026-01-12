package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.user.CreateUserDto;
import com.guilhermeleite.NotionCompass.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByNotionUserId(String notionUserId) {
        return userRepository.findByNotionUserId(notionUserId);
    }

    public User createUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setNotionUserId(createUserDto.notionId());
        user.setType(createUserDto.type());
        return userRepository.save(user);
    }

    public User findOrCreateUser(CreateUserDto createUserDto) {
        return this.findByNotionUserId(createUserDto.notionId())
                .orElseGet(() -> this.createUser(createUserDto));
    }

}
