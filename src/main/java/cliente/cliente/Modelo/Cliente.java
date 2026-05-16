package cliente.cliente.Modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@JsonFormat(pattern = "dd-MM-yyyy")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table (name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 13)
    private String run;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;


}
