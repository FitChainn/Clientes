package cliente.cliente.Service;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional

public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    private ClienteResponseDTO mapToDTO(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getRun(),
                cliente.getFechaNacimiento()
        );
    }

    public List<ClienteResponseDTO> obtenerClientes (){
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClienteResponseDTO> obtenerCliente(Long id){
        return clienteRepository.findById(id).map(this::mapToDTO);
    }
    public Cliente saveCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }
    public void eliminarPorId (Long id){clienteRepository.deleteById(id);}
}
