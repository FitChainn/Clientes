package cliente.cliente.Service;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional

public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    private WebClient.Builder webClientBuilder;

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
    public void eliminarPorId (Long id){clienteRepository.deleteById(id);}

    public ClienteResponseDTO saveCliente(ClienteRequestDTO dto) {
        // Validación: Consultar al microservicio Entrenador (puerto 8082)
        // Se asume que el endpoint /api/entrenadores/{id} existe en el otro servicio
        Boolean existeEntrenador = webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/entrenadores/{id}", dto.getEntrenador_Id())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .block();

        if (Boolean.FALSE.equals(existeEntrenador)) {
            throw new RuntimeException("El entrenador con ID " + dto.getEntrenador_Id() + " no existe.");
        }

        // Crear la entidad con el entrenador_id
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setRun(dto.getRun());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setEntrenadorId(dto.getEntrenador_Id());

        return mapToDTO(clienteRepository.save(cliente));
    }


}
