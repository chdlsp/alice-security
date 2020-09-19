package com.chdlsp.alice;

import com.chdlsp.alice.config.FileUploadConfig;
import com.chdlsp.alice.interfaces.util.JwtUtil;
import com.chdlsp.alice.service.FileUploadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootApplication
public class AliceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliceApplication.class, args);
	}

	// IoC 주입
	@Bean
	public JwtUtil createJwtUtil() {
		// key string 은 256 비트 (32글자) 이상이어야 함
		return new JwtUtil("1qaz2wsx3edc4rfv5tgb6yhn7ujm8ikk");
	}

}
