package com.celia.securetasksapi.dto;

import com.celia.securetasksapi.validation.NoInjectionLikeContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TaskRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ,.()_\\-]+$",
            message = "El título contiene caracteres no permitidos"
    )
    @NoInjectionLikeContent(message = "El título contiene contenido no permitido")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}