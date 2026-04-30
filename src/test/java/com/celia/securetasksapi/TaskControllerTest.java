package com.celia.securetasksapi;

import com.celia.securetasksapi.repository.TaskRepository;
import com.celia.securetasksapi.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired 
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(
                "test@example.com",
                "password",
                Role.ADMIN
        );

        userRepository.save(admin);
    }

    @Test
    void createTaskShouldWork() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Email", "test@example.com")
                        .content("""
                                {
                                  "title": "Preparar entrega final"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Preparar entrega final"));
    }

    @Test
    void createTaskShouldFailIfTitleHasDangerousCharacters() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "DROP TABLE users;"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"));
    }

    @Test
    void getTasksShouldReturnSavedTasksFromDatabase() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Email", "test@example.com")
                        .content("""
                                {
                                  "title": "Crear endpoint tareas"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks")
                    .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Crear endpoint tareas"));
    }
}
