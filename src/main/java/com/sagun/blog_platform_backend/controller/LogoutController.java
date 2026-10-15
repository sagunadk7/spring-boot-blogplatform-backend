package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.utils.RefreshCookieFactory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final RefreshCookieFactory refreshCookies;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE,refreshCookies.clear().toString()).build();
    }

}
