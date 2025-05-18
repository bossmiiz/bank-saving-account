package com.banking.savingaccount.config;

import com.banking.savingaccount.dto.RegisterRequest;
import com.banking.savingaccount.model.User;
import com.banking.savingaccount.model.UserRole;
import com.banking.savingaccount.repository.UserRepository;
import com.banking.savingaccount.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("teller1@bank.com").isEmpty()) {
            RegisterRequest tellerReq = new RegisterRequest();
            tellerReq.setEmail("teller1@bank.com");
            tellerReq.setPassword("teller123");
            tellerReq.setCitizenId("1111111111111");
            tellerReq.setThaiName("เทลเลอร์ หนึ่ง");
            tellerReq.setEnglishName("Teller One");
            tellerReq.setPin("1234");
            User teller = userService.register(tellerReq);
            teller.setRoles(Set.of(UserRole.ROLE_TELLER));
            userRepository.save(teller);
        }
    }
}