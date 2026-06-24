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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "CLIENTES", description = "GESTIÓN DE CLIENTES")
@Slf4j
@RestController
@RequestMapping("/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteModelAssembler assembler;

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "OBTENER TODOS LOS CLIENTES", description = "Retorna la lista de todos los clientes. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ClienteResponseDTO>>> obtenerClientes() {
        log.info("GET /v1/clientes - LISTAR TODOS");
        List<EntityModel<ClienteResponseDTO>> clientes = clienteService.obtenerClientes().stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(clientes,
                linkTo(methodOn(ClienteController.class).obtenerClientes()).withSelfRel()));
    }

    @Operation(summary = "OBTENER CLIENTE POR ID", description = "Retorna un cliente específico por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CLIENTE ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "CLIENTE NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> obtenerCliente(@PathVariable Long id) {
        log.info("GET /v1/clientes/{} - BUSCAR POR ID", id);
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
    public ResponseEntity<EntityModel<ClienteResponseDTO>> registrarCliente(@Valid @RequestBody ClienteRequestDTO nuevo) {
        log.info("POST /v1/clientes - REGISTRAR CLIENTE nombre={}", nuevo.getNombre());
        return ResponseEntity.status(201).body(assembler.toModel(clienteService.saveCliente(nuevo)));
    }

    @Operation(summary = "ELIMINAR CLIENTE", description = "Elimina un cliente por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "CLIENTE ELIMINADO CORRECTAMENTE"),
            @ApiResponse(responseCode = "404", description = "CLIENTE NO ENCONTRADO")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> eliminar(@PathVariable Long id) {
        log.info("DELETE /v1/clientes/{} - ELIMINAR CLIENTE", id);
        ClienteResponseDTO cliente = clienteService.obtenerCliente(id)
                .orElseThrow(() -> new NoSuchElementException("CLIENTE CON EL ID " + id + " NO ENCONTRADO"));
        clienteService.eliminarPorId(id);
        return ResponseEntity.ok(assembler.toModel(cliente));
    }

    @Operation(summary = "ASIGNAR ENTRENADOR (INTERNO)", description = "Asigna un entrenador a un cliente. Endpoint interno usado por WebClient, no pasa por Gateway.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ASIGNADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "CLIENTE O ENTRENADOR NO ENCONTRADO")
    })
    @PutMapping("/{clienteId}/asignar-entrenador/{entrenadorId}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> asignarEntrenadorInterno(
            @PathVariable Long clienteId,
            @PathVariable Long entrenadorId) {
        log.info("PUT /v1/clientes/{}/asignar-entrenador/{} - ASIGNAR ENTRENADOR (INTERNO)", clienteId, entrenadorId);
        return ResponseEntity.ok(assembler.toModel(clienteService.asignarEntrenadorInterno(clienteId, entrenadorId)));
    }

    @Operation(summary = "OBTENER CLIENTES POR ENTRENADOR", description = "Retorna todos los clientes asignados a un entrenador. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY CLIENTES PARA ESE ENTRENADOR")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<CollectionModel<EntityModel<ClienteResponseDTO>>> obtenerPorEntrenador(@PathVariable Long entrenadorId) {
        log.info("GET /v1/clientes/entrenador/{} - BUSCAR POR ENTRENADOR", entrenadorId);
        List<EntityModel<ClienteResponseDTO>> clientes = clienteService.obtenerClientesPorEntrenador(entrenadorId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(clientes,
                linkTo(methodOn(ClienteController.class).obtenerPorEntrenador(entrenadorId)).withSelfRel()));
    }

    @Operation(summary = "OBTENER CLIENTES POR ESTABLECIMIENTO", description = "Retorna todos los clientes de un establecimiento. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY CLIENTES PARA ESE ESTABLECIMIENTO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<CollectionModel<EntityModel<ClienteResponseDTO>>> obtenerPorEstablecimiento(@PathVariable Long establecimientoId) {
        log.info("GET /v1/clientes/establecimiento/{} - BUSCAR POR ESTABLECIMIENTO", establecimientoId);
        List<EntityModel<ClienteResponseDTO>> clientes = clienteService.obtenerPorEstablecimiento(establecimientoId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(clientes,
                linkTo(methodOn(ClienteController.class).obtenerPorEstablecimiento(establecimientoId)).withSelfRel()));
    }
}
