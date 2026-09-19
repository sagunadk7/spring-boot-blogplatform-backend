package com.sagun.blog_platform_backend.mapper;

import com.sagun.blog_platform_backend.dto.UserRegistrationResponseDto;
import com.sagun.blog_platform_backend.entity.User;

public class UserRegistrationRequestResponseMapper {

    public static UserRegistrationResponseDto toResponseDto(User user, String token){
        return new UserRegistrationResponseDto(user.getUsername(), token);
    }

}
