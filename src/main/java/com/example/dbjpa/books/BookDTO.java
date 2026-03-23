package com.example.dbjpa.books;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    //De este modo no pueden enviar un id mediante POST y solo sirve de lectura para el cliente (GET)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El titulo no puede estar vacío")
    private String title;

    @NotBlank(message = "El autor no puede estar vacío")
    private String author;

    @NotNull(message = "El año no puede estar vacío")
    private Integer year;
}
