package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;

import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, String> {
    Script findByNameEquals(String name);
}
