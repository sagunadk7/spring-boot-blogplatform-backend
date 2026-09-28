package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.dto.*;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.mapper.UserRegistrationRequestResponseMapper;
import com.sagun.blog_platform_backend.repository.UserRepository;
import com.sagun.blog_platform_backend.utils.EmailAndPasswordValidator;
import com.sagun.blog_platform_backend.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Objects;

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
    public UserRegistrationResponseDto responseOnSuccessfulRegistration(UserRegistrationRequestDto requestDto) {
        User user = createUser(requestDto);
        return UserRegistrationRequestResponseMapper.toResponseDto(true);
    }

    public String changePassword(String password){
        if(!EmailAndPasswordValidator.isStrongPassword(password)){
            throw new IllegalArgumentException();
        }

        String username = getAuthenticatedUser().getUsername();
        if(username == null){
            throw new RuntimeException("Internal Server error");
        }
        User user = repository.findByusername(username).orElseThrow(RuntimeException::new);
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


    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof User principal)){
            throw new RuntimeException("No authenticated user in context");
        }
        return repository.findByusername(principal.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Authenticated user no longer exists"));
    }

    public String updateEmail(UserEmailUpdateRequestDto requestDto){
        String username = getAuthenticatedUser().getUsername();
        if(username==null){
            throw new RuntimeException("Internal server error");
        }
        User user = repository.findByusername(username).orElseThrow(()-> new RuntimeException("User not found"));
        user.setEmail(requestDto.email());
        repository.save(user);
        return "Successfully updated an email ";
    }




}
