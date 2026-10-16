package com.sagun.blog_platform_backend.dto;

import java.util.List;

public record MeResponseDto(Long id, String username, String email, List<String> roles) {
}
