// src/main/java/com/miaueauau/clinica_veterinaria/dto/LoginRequest.java
package com.miaueauau.clinica_veterinaria.dto;

import lombok.Data; // Certifique-se de que o Lombok está no seu pom.xml

@Data // Anotação do Lombok para gerar getters, setters, toString, etc.
public class LoginRequest {
    private String email;
    private String password;
}