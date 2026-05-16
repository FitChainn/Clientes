package cliente.cliente.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El run no puede estar vacio")
    private String run;

    @NotBlank(message = "La fecha no puede esta vacia")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El ID del entrenador es obligatorio")
    private Long entrenadorId; // <-- FALTABA ESTO

}
