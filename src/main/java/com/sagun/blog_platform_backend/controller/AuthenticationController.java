package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.*;
import com.sagun.blog_platform_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto){
        return ResponseEntity.ok(userService.login(requestDto));
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDto> userRegistration(@RequestBody UserRegistrationRequestDto requestDto){
        return ResponseEntity.ok(userService.responseOnSuccessfulRegistration(requestDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody UserPasswordChangeRequestDto requestDto){
        String response = userService.changePassword(requestDto.newPassword());
        return ResponseEntity.ok(response);
    }

}
