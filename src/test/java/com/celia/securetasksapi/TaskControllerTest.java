package com.celia.securetasksapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.celia.securetasksapi.repository.TaskRepository;
import com.celia.securetasksapi.repository.UserRepository;
import com.celia.securetasksapi.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User(
                "test@example.com",
                passwordEncoder.encode("Password123!"),
                Role.USER
        );

        userRepository.save(user);

        token = jwtService.generateToken(user);
    }

    @Test
    void createTaskShouldWork() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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
                        .header("Authorization", "Bearer " + token)
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
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {
                                  "title": "Crear endpoint tareas"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Crear endpoint tareas"));
    }
}