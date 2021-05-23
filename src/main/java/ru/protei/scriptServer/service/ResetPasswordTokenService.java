package ru.protei.scriptServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.PasswordResetToken;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.PasswordTokenRepository;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Optional;

@Service
public class ResetPasswordTokenService {

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Transactional
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByTokenEquals(token).getUser());
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByTokenEquals(token);
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Transactional
    public void removePasswordResetToken(String token) {
        passwordTokenRepository.deleteByTokenEquals(token);
    }

    @Transactional
    public void removeAllUserPasswordResetTokens(User user) {
        passwordTokenRepository.deleteByUserEquals(user);
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
