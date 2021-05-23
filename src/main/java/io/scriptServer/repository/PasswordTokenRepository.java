package io.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.scriptServer.model.PasswordResetToken;
import io.scriptServer.model.User;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByTokenEquals(String token);

    void deleteByTokenEquals(String token);

    void deleteByUserEquals(User user);
}
