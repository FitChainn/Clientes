package cliente.cliente.Service;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional

public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getClientes (){
        return clienteRepository.findAll();
    }

    public Cliente getCliente(Long id){
        return clienteRepository.findById(id).
                orElseThrow(()->new RuntimeException("Cliente no encontrado con el id " +id));
    }
    public Cliente saveCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }
}
