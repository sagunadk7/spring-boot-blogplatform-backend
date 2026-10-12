package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.ConflictException;
import com.sagun.blog_platform_backend.customException.InvalidRequestException;
import com.sagun.blog_platform_backend.dto.*;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.enums.Role;
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

import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;

    public User create(UserRegistrationRequestDto requestDto, Set<Role> roles){
        String username = requestDto.username().trim();
        String email = requestDto.email().trim().toLowerCase(Locale.ROOT);
        if(!EmailAndPasswordValidator.isValidEmail(email)){
            throw new InvalidRequestException("Email address is not valid");
        }

        if(!EmailAndPasswordValidator.isStrongPassword(requestDto.password())){
            throw new InvalidRequestException("Password is too weak");
        }

        if(repository.existsByUsername(username)){
            throw new ConflictException("Username is already taken");
        }

        if(repository.existsByEmail(email)){
            throw new ConflictException("Email is already registered");
        }

        User user = new User(username,email,encoder.encode(requestDto.password()));
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return repository.save(user);

    }


    @Transactional
    public UserRegistrationResponseDto createWithRoles(UserRegistrationRequestDto requestDto, Set<Role> roles) {
        if(roles == null || roles.isEmpty()){
            throw new InvalidRequestException("At least one role is required");
        }
        return UserRegistrationResponseDto.from(create(requestDto,roles));
    }

    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto){
        return UserRegistrationResponseDto.from(create(requestDto, EnumSet.of(Role.USER)));
    }


    public String changePassword(String password){
        if(!EmailAndPasswordValidator.isStrongPassword(password)){
            throw new IllegalArgumentException();
        }

        String username = getAuthenticatedUser().getUsername();
        User user = repository.findByUsername(username).orElseThrow(RuntimeException::new);

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
