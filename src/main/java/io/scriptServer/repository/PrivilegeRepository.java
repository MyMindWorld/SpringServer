package io.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.scriptServer.model.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByNameEquals(String name);

    Privilege findByIdEquals(Long id);
}
