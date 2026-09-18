package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.dto.UserRegistrationRequestDto;
import com.sagun.blog_platform_backend.dto.UserRegistrationResponseDto;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.mapper.UserRegistrationRequestResponseMapper;
import com.sagun.blog_platform_backend.repository.UserRepository;
import com.sagun.blog_platform_backend.utils.EmailAndPasswordValidator;
import com.sagun.blog_platform_backend.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;

    public User createUser(UserRegistrationRequestDto requestDto){
        if(requestDto.password() == null || requestDto.username()==null || requestDto.email()==null){
            throw new IllegalArgumentException("Invalid User Registration Request 1");
        }
        if(!EmailAndPasswordValidator.isStrongPassword(requestDto.password()) || !EmailAndPasswordValidator.isValidEmail(requestDto.email())){
            throw new IllegalArgumentException("Invalid User Registration Request 2");
        }
        User user = new User();
        user.setEmail(requestDto.email().trim());
        user.setUsername(requestDto.username().trim());
        user.setPassword(encoder.encode(requestDto.password()));
        return repository.save(user);

    }
    public String generateJwtToken(String token){
        return jwtUtils.generateJwtToken(token);
    }

    @Transactional
    public UserRegistrationResponseDto responseOnSuccessfulRegistration(UserRegistrationRequestDto requestDto){
        User user = createUser(requestDto);
        String token = generateJwtToken(user.getUsername());
        return UserRegistrationRequestResponseMapper.toResponseDto(user,token);

    }

}
