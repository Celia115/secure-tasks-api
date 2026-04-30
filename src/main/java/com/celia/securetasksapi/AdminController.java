package com.celia.securetasksapi;

import com.celia.securetasksapi.repository.TaskRepository;
import com.celia.securetasksapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public final class AdminController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public AdminController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String emailHeader) {
        if (emailHeader == null || emailHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta cabecera X-User-Email");
        }

        User user = userRepository.findByEmailIgnoreCase(emailHeader.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin");
        }

        return user;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks(@RequestHeader("X-User-Email") String emailHeader) {
        getCurrentUser(emailHeader);
        return taskRepository.findAll();
    }
}