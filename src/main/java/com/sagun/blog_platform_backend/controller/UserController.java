package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.UserEmailUpdateRequestDto;
import com.sagun.blog_platform_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/update-email")
    public String updateProfile(@RequestBody UserEmailUpdateRequestDto requestDto){
        System.out.println("From controller --");
        return service.updateEmail(requestDto);
    }

}
