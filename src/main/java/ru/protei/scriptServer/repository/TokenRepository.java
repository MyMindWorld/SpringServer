package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.PersistentToken;

@Repository
public interface TokenRepository extends JpaRepository<PersistentToken, String> {
}
