package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.dto.*;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.mapper.UserRegistrationRequestResponseMapper;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.UserRepository;
import com.sagun.blog_platform_backend.utils.EmailAndPasswordValidator;
import com.sagun.blog_platform_backend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        user.setEnabled(true);
        user.setEmail(requestDto.email().trim());
        user.setUsername(requestDto.username().trim());
        user.setPasswordHash(encoder.encode(requestDto.password()));
        return repository.save(user);

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
        User user = repository.findByUsername(username).orElseThrow(RuntimeException::new);
        System.out.println("From ' changePassword ' author detail service: "+user.getUsername());

        if(Objects.equals(user.getPasswordHash(), password)){
            throw new IllegalArgumentException();
        }
        user.setPasswordHash(encoder.encode(password));
        repository.save(user);
        return "Password Changed successfully" + "Your new password is: "+ " "+password;
    }


    public UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response){
        String token = null;
        User user = repository.findByUsername(requestDto.username()).orElseThrow(()->new RuntimeException("User not found"));
        if(encoder.matches(requestDto.password(),user.getPasswordHash())){
            token  = jwtUtils.generateJwtToken(user.getUsername(),true);
        }
        if(token==null){
            throw new RuntimeException();
        }
        ResponseCookie cookie = ResponseCookie.from("refresh_token", jwtUtils.generateJwtToken(user.getUsername(), false))
                .httpOnly(true)
                .secure(true)
                .maxAge(7*24*60*60)
                .sameSite("strict")
                .path("/api/v1/auth/refresh-token")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new UserLoginResponseDto(token);
    }


    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)){
            throw new RuntimeException("No authenticated author in context");
        }
        return repository.findByUsername(principal.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Authenticated author no longer exists"));
    }

    public String updateEmail(UserEmailUpdateRequestDto requestDto){
        String username = getAuthenticatedUser().getUsername();
        User user = repository.findByUsername(username).orElseThrow(()-> new RuntimeException("User not found"));
        user.setEmail(requestDto.email());
        repository.save(user);
        return "Successfully updated an email ";
    }




}
