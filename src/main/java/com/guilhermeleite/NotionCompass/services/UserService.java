package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.user.UserDto;
import com.guilhermeleite.NotionCompass.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findOrCreateUser(UserDto userDto) {
        return userRepository.findById(userDto.id()).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(userDto.id());
            return userRepository.save(newUser);
        });
    }

}
