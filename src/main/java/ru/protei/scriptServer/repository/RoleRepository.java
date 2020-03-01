package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.Script;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNameEquals(String name);
}