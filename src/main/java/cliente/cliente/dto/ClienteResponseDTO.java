package cliente.cliente.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@JsonFormat(pattern = "dd-MM-yyyy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nombre;
    private String run;
    private LocalDate fechaNacimiento;
    private Long Entrenador_Id;

}
