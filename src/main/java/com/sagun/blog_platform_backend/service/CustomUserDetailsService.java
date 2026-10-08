package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        System.out.println("This is identifier: "+ identifier);
        User user = (identifier.contains("@")
                ? repository.findByEmail(identifier.trim().toLowerCase(Locale.ROOT))
                : repository.findByUsername(identifier.trim()))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return UserPrincipal.from(user);
    }
}
