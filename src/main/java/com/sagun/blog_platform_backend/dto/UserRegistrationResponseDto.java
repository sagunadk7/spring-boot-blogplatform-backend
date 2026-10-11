package com.sagun.blog_platform_backend.dto;

import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.enums.Role;

import java.util.Set;
import java.util.TreeSet;

public record UserRegistrationResponseDto(Long id, String username, String email, Set<Role> roles) {

    public static UserRegistrationResponseDto from (User user){
        return new UserRegistrationResponseDto(user.getId(), user.getUsername(),user.getEmail(),new TreeSet<>(user.getRoles()));
    }

}
