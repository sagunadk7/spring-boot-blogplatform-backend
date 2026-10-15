package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.dto.CategoryResponseDto;
import com.sagun.blog_platform_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> list(){
        return categoryRepository.findAll().stream().map(CategoryResponseDto::from).sorted(Comparator.comparing((CategoryResponseDto c) -> c.type().name())).toList();
    }

}
