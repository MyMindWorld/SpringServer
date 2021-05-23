package io.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.scriptServer.model.LogEntity;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
    List<LogEntity> findAllByOrderByDateDesc();
}
