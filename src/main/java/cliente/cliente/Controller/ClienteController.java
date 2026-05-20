package cliente.cliente.Controller;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.dto.ClienteEntrenador;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerClientes() {
        List<ClienteResponseDTO> clientes = clienteService.obtenerClientes();
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerCliente(@PathVariable Long id) {
        return clienteService.obtenerCliente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> RegistrarCliente(@Valid @RequestBody ClienteRequestDTO nuevo) {
        return ResponseEntity.status(201).body(clienteService.saveCliente(nuevo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{clienteId}/entrenador/{entrenadorId}")
    public ResponseEntity<ClienteResponseDTO> asignarEntrenador(
            @PathVariable Long clienteId,
            @PathVariable Long entrenadorId) {
        return ResponseEntity.ok(clienteService.asignarEntrenador(clienteId, entrenadorId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEntrenador(@PathVariable Long entrenadorId) {
        List<ClienteResponseDTO> clientes = clienteService.obtenerClientesPorEntrenador(entrenadorId);
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/entrenador/{entrenadorId}/simple")
    public ResponseEntity<List<ClienteEntrenador>> obtenerNombresPorEntrenador(@PathVariable Long entrenadorId) {
        List<ClienteEntrenador> clientes = clienteService.obtenerClientesPorEntrenador(entrenadorId)
                .stream()
                .map(c -> new ClienteEntrenador(c.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<List<ClienteResponseDTO>> obtenerPorEstablecimiento(
            @PathVariable Long establecimientoId) {
        List<ClienteResponseDTO> clientes = clienteService.obtenerPorEstablecimiento(establecimientoId);
        if (clientes.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(clientes);
    }
}
