package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.dto.UserLoginRequestDto;
import com.sagun.blog_platform_backend.dto.UserLoginResponseDto;
import com.sagun.blog_platform_backend.dto.UserRegistrationRequestDto;
import com.sagun.blog_platform_backend.dto.UserRegistrationResponseDto;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.mapper.UserRegistrationRequestResponseMapper;
import com.sagun.blog_platform_backend.repository.UserRepository;
import com.sagun.blog_platform_backend.utils.EmailAndPasswordValidator;
import com.sagun.blog_platform_backend.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public String changePassword(String password){
        if(!EmailAndPasswordValidator.isStrongPassword(password)){
            throw new IllegalArgumentException();
        }

        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof User user){
            username = user.getUsername();
        }
        if(username == null){
            throw new RuntimeException("Internal Server error");
        }
        User user = repository.findByusername(username).orElseThrow(() -> new RuntimeException());
        System.out.println("From ' changePassword ' user detail service: "+user.getUsername());

        if(user.getPassword().equals(password)){
            throw new IllegalArgumentException();
        }
        user.setPassword(encoder.encode(password));
        repository.save(user);
        return "Password Changed successfully" + "Your new password is: "+ " "+password;
    }


    public UserLoginResponseDto login(UserLoginRequestDto requestDto){
        String token = null;
        User user = repository.findByusername(requestDto.username()).orElseThrow(()->new RuntimeException("User not found"));
        if(encoder.matches(requestDto.password(),user.getPassword())){
            token  = jwtUtils.generateJwtToken(user.getUsername());
        }
        if(token==null){
            throw new RuntimeException();
        }
        return new UserLoginResponseDto(token);
    }

}
