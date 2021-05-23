package io.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.scriptServer.model.PersistentToken;

@Repository
public interface TokenRepository extends JpaRepository<PersistentToken, String> {
}
