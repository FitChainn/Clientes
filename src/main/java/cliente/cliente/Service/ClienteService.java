package cliente.cliente.Service;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
                cliente.getFechaNacimiento(),
                cliente.getEntrenadorId()
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
    public ClienteResponseDTO saveCliente(ClienteRequestDTO dto) {
        // Petición HTTP GET al microservicio de Entrenadores
        Boolean existeEntrenador = webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/entrenadores/{id}", dto.getEntrenadorId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new RuntimeException("El entrenador con ID " + dto.getEntrenadorId() + " no existe.")))
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .block(); // .block() congela la ejecución hasta recibir la respuesta (Síncrono)

        if (Boolean.FALSE.equals(existeEntrenador)) {
            throw new RuntimeException("No se pudo validar el entrenador.");
        }

        // Si el entrenador existe, construimos la entidad y guardamos en MySQL
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setRun(dto.getRun());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setId(dto.getEntrenadorId()); // Guardamos la referencia del ID

        return mapToDTO(clienteRepository.save(cliente));
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
