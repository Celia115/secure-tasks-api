package com.celia.securetasksapi.dto;

import com.celia.securetasksapi.validation.NoInjectionLikeContent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email es demasiado largo")
    @NoInjectionLikeContent(message = "El email contiene contenido no permitido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 10, max = 64, message = "La contraseña debe tener entre 10 y 64 caracteres")
    @NoInjectionLikeContent(message = "La contraseña contiene contenido no permitido")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}