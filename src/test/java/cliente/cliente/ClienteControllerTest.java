package cliente.cliente;

import cliente.cliente.Controller.ClienteController;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.config.SecurityConfig;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import cliente.cliente.filter.RolHeaderFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteResponseDTO cResponse;
    private ClienteRequestDTO cRequest;

    @BeforeEach
    void setUp() {
        cResponse = new ClienteResponseDTO(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, null, 3L);
        cRequest = new ClienteRequestDTO("JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
    }

    @Test
    void Post_registrar201() throws Exception {
        when(clienteService.saveCliente(any(ClienteRequestDTO.class))).thenReturn(cResponse);

        mockMvc.perform(post("/v1/clientes")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("JUANITO PEREZ"));
    }

    @Test
    void Get_obtenerClientes() throws Exception {
        when(clienteService.obtenerClientes()).thenReturn(List.of(cResponse));

        mockMvc.perform(get("/v1/clientes")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("JUANITO PEREZ"));
    }

    @Test
    void Get_obtenerClientePorId() throws Exception {
        when(clienteService.obtenerCliente(1L)).thenReturn(Optional.of(cResponse));

        mockMvc.perform(get("/v1/clientes/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("JUANITO PEREZ"));
    }

    @Test
    void Get_obtenerClienteNotFound() throws Exception {
        when(clienteService.obtenerCliente(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/clientes/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void Delete_eliminarCliente() throws Exception {
        when(clienteService.obtenerCliente(1L)).thenReturn(Optional.of(cResponse));

        mockMvc.perform(delete("/v1/clientes/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Eliminado correctamente"));
    }
}
