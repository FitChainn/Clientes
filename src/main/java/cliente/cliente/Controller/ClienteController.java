package cliente.cliente.Controller;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
    public ResponseEntity<ClienteResponseDTO> RegistrarCliente(@Valid @RequestBody ClienteRequestDTO nuevo)
    {
        return ResponseEntity.status(201).body(clienteService.saveCliente(nuevo));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminar (@PathVariable Long id){
        if(clienteService.obtenerCliente(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

}
