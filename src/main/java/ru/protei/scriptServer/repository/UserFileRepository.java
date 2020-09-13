package ru.protei.scriptServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.protei.scriptServer.model.UserFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
    UserFile findByNameEquals(String name);

    UserFile findByIdEquals(Long id);

    UserFile findByFullPathEquals(String fullPath);

    UserFile findByNameEqualsAndScriptEquals(@NotBlank String name, String script);

    List<UserFile> findAllByScriptIn(List<String> script);
}