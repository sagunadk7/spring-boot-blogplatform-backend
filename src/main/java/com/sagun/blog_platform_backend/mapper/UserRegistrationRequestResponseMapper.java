package com.sagun.blog_platform_backend.mapper;

import com.sagun.blog_platform_backend.dto.UserRegistrationResponseDto;
import com.sagun.blog_platform_backend.entity.User;

public class UserRegistrationRequestResponseMapper {

    public static UserRegistrationResponseDto toResponseDto(User user, String token){
        System.out.println("From mapper Username: "+user.getUsername());
        return new UserRegistrationResponseDto(user.getUsername(), token);
    }

}
