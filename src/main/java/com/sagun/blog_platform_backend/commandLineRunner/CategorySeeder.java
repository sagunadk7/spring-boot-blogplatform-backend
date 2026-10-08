package com.sagun.blog_platform_backend.commandLineRunner;

import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.enums.CategoryType;
import com.sagun.blog_platform_backend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
public class CategorySeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    @Override
    public void run(String... args) throws Exception {
        for(CategoryType type : CategoryType.values()){
            if(!categoryRepository.existsByType(type)){
                categoryRepository.save(new Category(type,getSlug(type.toString())));
            }
        }
        System.out.println("Command line runner ran successfully");
    }

    private String getSlug(String type){
        return type.toLowerCase(Locale.ROOT).replace("_","-");
    }
}
