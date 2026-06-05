package cliente.cliente.Service;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import cliente.cliente.WebClient.EntrenadorClient;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EntrenadorClient entrenadorClient;

    private ClienteResponseDTO mapToDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getRun(),
                cliente.getFechaNacimiento(),
                cliente.getEntrenadorId(),
                null,
                cliente.getEstablecimientoId()
        );
    }

    private ClienteResponseDTO mapToDTOConEntrenador(Cliente cliente) {
        ClienteResponseDTO dto = mapToDTO(cliente);
        if (cliente.getEntrenadorId() != null) {
            dto.setEntrenador(entrenadorClient.obtenerEntrenadorSimple(cliente.getEntrenadorId()));
        }
        return dto;
    }

    public List<ClienteResponseDTO> obtenerClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToDTOConEntrenador)
                .collect(Collectors.toList());
    }

    public Optional<ClienteResponseDTO> obtenerCliente(Long id) {
        return clienteRepository.findById(id).map(this::mapToDTOConEntrenador);
    }

    public ClienteResponseDTO saveCliente(ClienteRequestDTO dto) {
        log.info("Guardando cliente: {}", dto.getNombre());
        entrenadorClient.verificarEntrenadorExiste(dto.getEntrenadorId());

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setRun(dto.getRun());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setEntrenadorId(dto.getEntrenadorId());
        cliente.setEstablecimientoId(dto.getEstablecimientoId());
        log.info("Cliente guardado con ID: {}", cliente.getId());
        return mapToDTO(clienteRepository.save(cliente));
    }

    // Método interno — solo lo llama Entrenador via WebClient
    public ClienteResponseDTO asignarEntrenadorInterno(Long clienteId, Long entrenadorId) {
        log.info("Asignando entrenador ID: {} al cliente ID: {} (llamada interna)", entrenadorId, clienteId);
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + clienteId + " no encontrado"));
        cliente.setEntrenadorId(entrenadorId);
        return mapToDTO(clienteRepository.save(cliente));
    }

    public void eliminarPorId(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        clienteRepository.deleteById(id);
    }

    public List<ClienteResponseDTO> obtenerClientesPorEntrenador(Long entrenadorId) {
        return clienteRepository.findByEntrenadorId(entrenadorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteResponseDTO> obtenerPorEstablecimiento(Long establecimientoId) {
        return clienteRepository.findByEstablecimientoId(establecimientoId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}