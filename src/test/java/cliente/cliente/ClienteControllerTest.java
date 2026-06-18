package cliente.cliente;

import cliente.cliente.Controller.ClienteController;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.assembler.ClienteModelAssembler;
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

import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private ClienteModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteResponseDTO cResponse;
    private ClienteRequestDTO cRequest;

    @BeforeEach
    void setUp() {
        cResponse = new ClienteResponseDTO(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, null, 3L);
        cRequest = new ClienteRequestDTO("JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        when(assembler.toModel(any(ClienteResponseDTO.class))).thenAnswer(inv -> EntityModel.of(inv.getArgument(0)));
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
                .andExpect(jsonPath("$.nombre").value("JUANITO PEREZ"))
                .andExpect(jsonPath("$.run").value("12.123.431-2"));
    }

    @Test
    void POST_validation_registrar400() throws Exception {
        ClienteRequestDTO reqInvalido = new ClienteRequestDTO();

        mockMvc.perform(post("/v1/clientes")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void Get_obtenerClientes200() throws Exception {
        when(clienteService.obtenerClientes()).thenReturn(List.of(cResponse));

        mockMvc.perform(get("/v1/clientes")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("JUANITO PEREZ"));
    }

    @Test
    void Get_obtenerClientes204_listaVacia() throws Exception {
        when(clienteService.obtenerClientes()).thenReturn(List.of());

        mockMvc.perform(get("/v1/clientes")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }


    @Test
    void Get_obtenerClientePorId200() throws Exception {
        when(clienteService.obtenerCliente(1L)).thenReturn(Optional.of(cResponse));

        mockMvc.perform(get("/v1/clientes/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("JUANITO PEREZ"))
                .andExpect(jsonPath("$.run").value("12.123.431-2"));
    }

    @Test
    void Get_obtenerClienteNotFound404() throws Exception {
        when(clienteService.obtenerCliente(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/clientes/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("CLIENTE CON EL ID 99 NO ENCONTRADO"));
    }


    @Test
    void Get_obtenerPorEntrenador200() throws Exception {
        when(clienteService.obtenerClientesPorEntrenador(2L)).thenReturn(List.of(cResponse));

        mockMvc.perform(get("/v1/clientes/entrenador/2")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].entrenadorId").value(2L));
    }

    @Test
    void Get_obtenerPorEntrenador204_sinClientes() throws Exception {
        when(clienteService.obtenerClientesPorEntrenador(99L)).thenReturn(List.of());

        mockMvc.perform(get("/v1/clientes/entrenador/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }


    @Test
    void Get_obtenerPorEstablecimiento200() throws Exception {
        when(clienteService.obtenerPorEstablecimiento(3L)).thenReturn(List.of(cResponse));

        mockMvc.perform(get("/v1/clientes/establecimiento/3")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].establecimientoId").value(3L));
    }

    @Test
    void Get_obtenerPorEstablecimiento204_sinClientes() throws Exception {
        when(clienteService.obtenerPorEstablecimiento(99L)).thenReturn(List.of());

        mockMvc.perform(get("/v1/clientes/establecimiento/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }


    @Test
    void Put_asignarEntrenador200() throws Exception {
        when(clienteService.asignarEntrenadorInterno(1L, 2L)).thenReturn(cResponse);

        mockMvc.perform(put("/v1/clientes/1/asignar-entrenador/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.entrenadorId").value(2L));
    }


    @Test
    void Delete_eliminarCliente200() throws Exception {
        when(clienteService.obtenerCliente(1L)).thenReturn(Optional.of(cResponse));

        mockMvc.perform(delete("/v1/clientes/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Eliminado correctamente"));

        verify(clienteService).eliminarPorId(1L);
    }

    @Test
    void Delete_eliminarCliente404_noExiste() throws Exception {
        when(clienteService.obtenerCliente(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/v1/clientes/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado"));
    }
}