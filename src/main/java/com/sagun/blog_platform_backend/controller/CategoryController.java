package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.CategoryResponseDto;
import com.sagun.blog_platform_backend.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryResponseDto> list(){
        return service.list();
    }

}
