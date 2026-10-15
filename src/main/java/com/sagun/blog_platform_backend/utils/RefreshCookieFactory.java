package com.sagun.blog_platform_backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshCookieFactory {

    private static final String NAME = "refresh_token";
    private static final String PATH = "/api/v1/auth/refresh-token";

    private final boolean secure;
    private final Duration maxAge;

    public RefreshCookieFactory(@Value("${app.refresh-cookie.secure.:true}") boolean secure,
                                @Value("${app.refresh-cookie.max-age-days:7}")long maxAgeDays){
        this.secure=secure;
        this.maxAge=Duration.ofDays(maxAgeDays);
    }

    public ResponseCookie create(String refreshToken){
        return build(refreshToken,maxAge);
    }

    public ResponseCookie clear(){
        return build("",Duration.ZERO);
    }

    private final ResponseCookie build(String value,Duration age){
        return ResponseCookie.from(NAME,value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("strict")
                .path(PATH)
                .maxAge(maxAge)
                .build();
    }
}
