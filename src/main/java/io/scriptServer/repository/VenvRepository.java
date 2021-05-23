package io.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.scriptServer.model.Venv;

@Repository
public interface VenvRepository extends JpaRepository<Venv, String> {
    Venv findByNameEquals(String name);
}
