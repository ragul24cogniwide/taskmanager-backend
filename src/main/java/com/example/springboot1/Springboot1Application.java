package com.example.springboot1;

import com.example.springboot1.model.Users;
import com.example.springboot1.repo.UserRepository;
import com.example.springboot1.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Springboot1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springboot1Application.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository repo, UserService service) {
		return args -> {
			if (repo.findByEmail("admin") == null) {
				Users admin = new Users();
				admin.setUsername("Admin");
				admin.setEmailid("admin");
				admin.setPassword("admin@123");
				admin.setRole("ADMIN");
				admin.setConfirmpassword("admin@123");
				repo.save(admin);
				service.setRegister(admin);
			}
		};
	}

}
