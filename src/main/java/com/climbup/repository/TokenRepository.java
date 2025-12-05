package com.climbup.repository;

import com.climbup.model.PasswordResetToken;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find token by its string value
    PasswordResetToken findByToken(String token);

    // Find token by user (so we can delete old one before creating new)
    PasswordResetToken findByUser(User user);

    // Optional: delete all expired tokens
    // void deleteByExpiryDateBefore(LocalDateTime now);
}