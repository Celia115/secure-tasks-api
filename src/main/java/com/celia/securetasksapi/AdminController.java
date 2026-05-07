package com.celia.securetasksapi;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.celia.securetasksapi.repository.TaskRepository;
import com.celia.securetasksapi.repository.UserRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public AdminController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        User user = (User) authentication.getPrincipal();

        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin");
        }

        return user;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks(Authentication authentication) {
        getCurrentUser(authentication);
        return taskRepository.findAll();
    }
}