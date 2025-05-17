package com.banking.savingaccount.repository;

import com.banking.savingaccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByCitizenId(String citizenId);

    boolean existsByEmail(String email);

    boolean existsByCitizenId(String citizenId);
}