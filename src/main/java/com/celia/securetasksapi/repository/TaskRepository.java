package com.celia.securetasksapi.repository;

import com.celia.securetasksapi.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
