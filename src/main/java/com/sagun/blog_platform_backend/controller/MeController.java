package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.MeResponseDto;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class MeController {

    @GetMapping("/me")
    public MeResponseDto me(@AuthenticationPrincipal UserPrincipal me){
        List<String> roles = me.getAuthorities().stream().map(a -> a.getAuthority().replaceFirst("^ROLE_", "")).sorted().toList();
        return new MeResponseDto(me.getId(),me.getUsername(),me.getEmail(),roles);
    }

}
