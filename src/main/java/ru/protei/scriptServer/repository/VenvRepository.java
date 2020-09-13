package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.Venv;

@Repository
public interface VenvRepository extends JpaRepository<Venv, String> {
    Venv findByNameEquals(String name);
}
