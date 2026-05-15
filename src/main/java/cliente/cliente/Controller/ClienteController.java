package cliente.cliente.Controller;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.dto.ClienteResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity <List<ClienteResponseDTO>> obtenerClientes(){
        List<ClienteResponseDTO> clientes = clienteService.obtenerClientes();
        if(clientes.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);

    }
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerCliente (@PathVariable Long id){
        return clienteService.obtenerCliente(id).
                map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> RegistrarCliente(@RequestBody Cliente nuevo){
        return ResponseEntity.status(201).body(clienteService.saveCliente(nuevo));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar (@PathVariable Long id){
        if(clienteService.obtenerCliente(id).isEmpty()){
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

}
