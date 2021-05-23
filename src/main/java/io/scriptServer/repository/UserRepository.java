package io.scriptServer.repository;

import io.scriptServer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameEquals(String username);

    User findByEmailEquals(@NotBlank String email);
}