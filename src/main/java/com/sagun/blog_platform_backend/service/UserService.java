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
import com.sagun.blog_platform_backend.utils.RefreshCookieFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final RefreshCookieFactory refreshCookies;

    public User create(UserRegistrationRequestDto requestDto){
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
        return repository.save(user);

    }



    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto){
        return UserRegistrationResponseDto.from(create(requestDto));
    }


    public String changePassword(String password){
        if(!EmailAndPasswordValidator.isStrongPassword(password)){
            throw new IllegalArgumentException("Password is too weak");
        }

        User user = getAuthenticatedUser();
        if(encoder.matches(password,user.getPasswordHash())){
            throw new InvalidRequestException("The new password must be different from the current one");
        }
        user.setPasswordHash(encoder.encode(password));
        repository.save(user);
        return "Password changed successfully";
    }


    public UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response){
        String identifier = requestDto.username().trim();
        User user = (identifier.contains("@") ? repository.findByEmail(identifier.toLowerCase(Locale.ROOT)) : repository.findByUsername(identifier)).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if(!encoder.matches(requestDto.password(), user.getPasswordHash())){
            throw new BadCredentialsException("Invalid credentials");
        }
        if(!user.isEnabled()){
            throw new DisabledException("Account is disabled");
        }
        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), true);
        String refreshToken = jwtUtils.generateJwtToken(user.getUsername(), false);
        response.addHeader(HttpHeaders.SET_COOKIE,refreshCookies.create(refreshToken).toString());
        return new UserLoginResponseDto(accessToken);
    }


    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)){
            throw new RuntimeException("No authenticated author in context");
        }
        return repository.findByUsername(principal.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Authenticated author no longer exists"));
    }

    public String updateEmail(UserEmailUpdateRequestDto requestDto){
        String email = requestDto.email().toLowerCase(Locale.ROOT);
        if (!EmailAndPasswordValidator.isValidEmail(email)) {
            throw new InvalidRequestException("Email address is not valid");
        }
        User user = getAuthenticatedUser();
        if(!email.equals(user.getEmail()) && repository.existsByEmail(email)){
            throw new ConflictException("Email is already registered");
        }
        user.setEmail(email);
        repository.save(user);
        return "Successfully updated an email ";
    }




}
