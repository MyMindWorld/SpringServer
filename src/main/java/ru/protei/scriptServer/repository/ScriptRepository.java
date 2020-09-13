package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.Script;

@Repository
public interface ScriptRepository extends JpaRepository<Script, String> {
    Script findByNameEquals(String name);

    Script findByDisplayNameEquals(String name);
}
