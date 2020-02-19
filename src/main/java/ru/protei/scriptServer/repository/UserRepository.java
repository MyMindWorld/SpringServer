package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}