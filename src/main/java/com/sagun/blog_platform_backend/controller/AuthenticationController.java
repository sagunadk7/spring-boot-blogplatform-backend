package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.UserRegistrationRequestDto;
import com.sagun.blog_platform_backend.dto.UserRegistrationResponseDto;
import com.sagun.blog_platform_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "Hello this is login";
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDto> userRegistration(@RequestBody UserRegistrationRequestDto requestDto){
        return ResponseEntity.ok(userService.responseOnSuccessfulRegistration(requestDto));
    }

    @PostMapping("/change-password")
    public String changePassword(){
        return "Password Changed";
    }

}
