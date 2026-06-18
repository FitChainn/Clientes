package cliente.cliente.Controller;

import cliente.cliente.Service.ClienteService;
import cliente.cliente.assembler.ClienteModelAssembler;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "CLIENTES", description = "GESTIÓN DE CLIENTES")
@Slf4j
@RestController
@RequestMapping("v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteModelAssembler assembler;

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "OBTENER TODOS LOS CLIENTES", description = "Retorna la lista de todos los clientes. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY CLIENTES REGISTRADOS"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerClientes() {
        List<ClienteResponseDTO> clientes = clienteService.obtenerClientes();
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "OBTENER CLIENTE POR ID", description = "Retorna un cliente específico por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CLIENTE ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "CLIENTE NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> obtenerCliente(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.obtenerCliente(id)
                .orElseThrow(() -> new NoSuchElementException("CLIENTE CON EL ID " + id + " NO ENCONTRADO"));
        return ResponseEntity.ok(assembler.toModel(cliente));
    }

    @Operation(summary = "REGISTRAR CLIENTE", description = "Crea un nuevo cliente. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CLIENTE REGISTRADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "400", description = "DATOS INVÁLIDOS"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@Valid @RequestBody ClienteRequestDTO nuevo) {
        return ResponseEntity.status(201).body(clienteService.saveCliente(nuevo));
    }

    @Operation(summary = "ELIMINAR CLIENTE", description = "Elimina un cliente por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CLIENTE ELIMINADO CORRECTAMENTE"),
            @ApiResponse(responseCode = "404", description = "CLIENTE NO ENCONTRADO")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (clienteService.obtenerCliente(id).isEmpty()) {
            Map<String, String> mensaje1 = new HashMap<>();
            mensaje1.put("mensaje", "Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje1);
        }
        clienteService.eliminarPorId(id);
        log.info("Cliente eliminado");
        Map<String, String> mensaje = new HashMap<>();
        mensaje.put("mensaje", "Eliminado correctamente");
        return ResponseEntity.ok(mensaje);
    }

    // Endpoint interno — usado por Entrenador via WebClient, no aparece en Gateway
    @Operation(summary = "ASIGNAR ENTRENADOR (INTERNO)", description = "Asigna un entrenador a un cliente. Endpoint interno usado por WebClient, no pasa por Gateway.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ASIGNADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "CLIENTE O ENTRENADOR NO ENCONTRADO")
    })
    @PutMapping("/{clienteId}/asignar-entrenador/{entrenadorId}")
    public ResponseEntity<ClienteResponseDTO> asignarEntrenadorInterno(
            @PathVariable Long clienteId,
            @PathVariable Long entrenadorId) {
        return ResponseEntity.ok(clienteService.asignarEntrenadorInterno(clienteId, entrenadorId));
    }

    @Operation(summary = "OBTENER CLIENTES POR ENTRENADOR", description = "Retorna todos los clientes asignados a un entrenador. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY CLIENTES PARA ESE ENTRENADOR")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEntrenador(@PathVariable Long entrenadorId) {
        List<ClienteResponseDTO> clientes = clienteService.obtenerClientesPorEntrenador(entrenadorId);
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "OBTENER CLIENTES POR ESTABLECIMIENTO", description = "Retorna todos los clientes de un establecimiento. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY CLIENTES PARA ESE ESTABLECIMIENTO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEstablecimiento(@PathVariable Long establecimientoId) {
        List<ClienteResponseDTO> clientes = clienteService.obtenerPorEstablecimiento(establecimientoId);
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }
}