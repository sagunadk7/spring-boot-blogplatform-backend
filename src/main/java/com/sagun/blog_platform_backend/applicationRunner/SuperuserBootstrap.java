package com.sagun.blog_platform_backend.applicationRunner;

import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.enums.Role;
import com.sagun.blog_platform_backend.repository.UserRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SuperuserBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperuserBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final String username;
    private final String email;
    private final String password;

    public SuperuserBootstrap(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.bootstrap-superuser.username:}") String username,
                              @Value("${app.bootstrap-superuser.email:}") String email,
                              @Value("${app.bootstrap-superuser.password:}") String password){
        this.userRepository = userRepository;
        this.encoder=passwordEncoder;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(email + password + username);
        log.info("SuperuserBootstrap running (username blank: {}, email blank: {}, password blank: {})", username.isBlank(), email.isBlank(), password.isBlank());
      if(username.isBlank() || email.isBlank() || password.isBlank()){
          log.info("Superuser bootstrap skipped: bootstrap-superuser settings are not all set");
          return;
      }
      if(userRepository.existsByUsername(username.trim())){
          log.info("Superuser bootstrap skipped: user '{}' already exists", username);
          return;
      }
      if(userRepository.existsByEmail(email.trim())){
          log.info("Superuser bootstrap skipped: email '{}' already exists", email);
          return;
      }
      User owner = new User(username.trim(), email.trim().toLowerCase(Locale.ROOT), encoder.encode(password));
      owner.getRoles().add(Role.SUPERUSER);
      userRepository.save(owner);
      log.warn("Superuser '{}' created, Remove the bootstrap settings from the environment now.",username);
    }
}
