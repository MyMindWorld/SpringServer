package ru.protei.scriptServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.protei.scriptServer.exception.UserNotFoundException;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
public class RestApiController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/api/users")
    public List<User> getAllNotes() {
        return userRepository.findAll();
    }

    @PostMapping("/api/users")
    public User createNote(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/api/users/{id}")
    public User getNoteById(@PathVariable(value = "id") Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @PutMapping("/api/users/{id}")
    public User updateNote(@PathVariable(value = "id") Long userId,
                           @Valid @RequestBody User userRequest) throws UserNotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setUsername(userRequest.getUsername());
        user.setLdapName(userRequest.getLdapName());
        user.setEmail(userRequest.getEmail());

        return userRepository.save(user);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<?> deleteDeveloper(@PathVariable(value = "id") Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }
}
