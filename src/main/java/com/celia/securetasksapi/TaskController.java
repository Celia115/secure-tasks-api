package com.celia.securetasksapi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.celia.securetasksapi.dto.TaskRequest;
import com.celia.securetasksapi.repository.TaskRepository;
import com.celia.securetasksapi.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private static final String TASK_NOT_FOUND = "Tarea no encontrada";

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String emailHeader) {
        if (emailHeader == null || emailHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta cabecera X-User-Email");
        }

        return userRepository.findByEmailIgnoreCase(emailHeader.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Task> getMyTasks(@RequestHeader("X-User-Email") String emailHeader) {
        User currentUser = getCurrentUser(emailHeader);

        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findAll();
        }

        return taskRepository.findByOwner(currentUser);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Task getTaskById(@PathVariable Long id,
            @RequestHeader("X-User-Email") String emailHeader) {
        User currentUser = getCurrentUser(emailHeader);

        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, TASK_NOT_FOUND));
        }

        return taskRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a esta tarea"));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody TaskRequest request,
            @RequestHeader("X-User-Email") String emailHeader) {
        User currentUser = getCurrentUser(emailHeader);

        Task task = new Task(request.getTitle().trim(), currentUser);
        log.info("Tarea creada por usuario: {}", currentUser.getEmail());
        return taskRepository.save(task);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Task updateTask(@PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("X-User-Email") String emailHeader) {
        User currentUser = getCurrentUser(emailHeader);

        Task task;

        if (currentUser.getRole() == Role.ADMIN) {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, TASK_NOT_FOUND));
        } else {
            task = taskRepository.findByIdAndOwner(id, currentUser)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar esta tarea"));
        }

        task.setTitle(request.getTitle().trim());
        log.info("Tarea actualizada. id={}, usuario={}", id, currentUser.getEmail());
        return taskRepository.save(task);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id,
            @RequestHeader("X-User-Email") String emailHeader) {
        User currentUser = getCurrentUser(emailHeader);

        Task task;

        if (currentUser.getRole() == Role.ADMIN) {
            task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, TASK_NOT_FOUND));
        } else {
            task = taskRepository.findByIdAndOwner(id, currentUser)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes borrar esta tarea"));
        }

        log.info("Tarea eliminada. id={}, usuario={}", id, currentUser.getEmail());
        taskRepository.delete(task);
    }
}
