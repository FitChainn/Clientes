package cliente.cliente.Controller;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Service.ClienteService;
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
    public ResponseEntity <List<Cliente>> obtenerClientes(){
        List<Cliente> clientes = clienteService.getClientes();
        if(clientes.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);

    }
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente (@PathVariable Long id){
        try{
            Cliente cliente = clienteService.getCliente(id);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Cliente> RegistrarCliente(@RequestBody Cliente nuevo){
        return ResponseEntity.status(201).body(clienteService.saveCliente(nuevo));
    }

}
