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
    @Autowired
    private WebClient.Builder webClientBuilder;

    private ClienteResponseDTO mapToDTO(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getRun(),
                cliente.getFechaNacimiento(),
                cliente.getEntrenadorId(),
                null
        );
    }

    public List<ClienteResponseDTO> obtenerClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(cliente -> {
                    ClienteResponseDTO dto = mapToDTO(cliente);
                    if (cliente.getEntrenadorId() != null) {
                        try {
                            Object entrenador = webClientBuilder.build()
                                    .get()
                                    .uri("http://localhost:8082/api/entrenadores/{id}/simple", cliente.getEntrenadorId())
                                    .retrieve()
                                    .bodyToMono(Object.class)
                                    .block();
                            dto.setEntrenador(entrenador);
                        } catch (Exception e) {
                            dto.setEntrenador(null);
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<ClienteResponseDTO> obtenerCliente(Long id) {
        return clienteRepository.findById(id).map(cliente -> {
            ClienteResponseDTO dto = mapToDTO(cliente);
            if (cliente.getEntrenadorId() != null) {
                try {
                    Object entrenador = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8082/api/entrenadores/{id}/simple", cliente.getEntrenadorId())
                            .retrieve()
                            .bodyToMono(Object.class)
                            .block();
                    dto.setEntrenador(entrenador);
                } catch (Exception e) {
                    dto.setEntrenador(null);
                }
            }
            return dto;
        });
    }


    public ClienteResponseDTO saveCliente(ClienteRequestDTO dto) {
        // Petición HTTP GET al microservicio de Entrenadores
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/entrenadores/{id}", dto.getEntrenadorId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new RuntimeException("El entrenador con ID " + dto.getEntrenadorId() + " no existe.")))
                .toBodilessEntity()
                .block(); // .block() congela la ejecución hasta recibir la respuesta (Síncrono)
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setRun(dto.getRun());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setEntrenadorId(dto.getEntrenadorId()); // Guardamos la referencia del ID

        return mapToDTO(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO asignarEntrenador(Long clienteId, Long entrenadorId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/entrenadores/{id}", entrenadorId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new RuntimeException("El entrenador con ID " + entrenadorId + " no existe.")))
                .toBodilessEntity()
                .block();

        cliente.setEntrenadorId(entrenadorId);
        return mapToDTO(clienteRepository.save(cliente));
    }
    public void eliminarPorId (Long id){clienteRepository.deleteById(id);}

    public List<ClienteResponseDTO> obtenerClientesPorEntrenador(Long entrenadorId) {
        return clienteRepository.findByEntrenadorId(entrenadorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
