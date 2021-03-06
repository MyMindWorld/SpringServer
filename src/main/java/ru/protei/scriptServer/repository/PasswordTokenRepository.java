package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.PasswordResetToken;
import ru.protei.scriptServer.model.User;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByTokenEquals(String token);

    void deleteByTokenEquals(String token);

    void deleteByUserEquals(User user);
}
