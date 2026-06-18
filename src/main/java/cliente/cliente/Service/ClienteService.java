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

    private final ClienteRepository clienteRepository;

    private final EntrenadorClient entrenadorClient;

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
            try {
                dto.setEntrenador(entrenadorClient.obtenerEntrenadorSimple(cliente.getEntrenadorId()));
                log.info("ENTRENADOR CON ID {} OBTENIDO CORRECTAMENTE PARA CLIENTE {}", cliente.getEntrenadorId(), cliente.getId());
            } catch (Exception e) {
                log.warn("NO SE PUDO OBTENER EL ENTRENADOR CON ID {} PARA CLIENTE {}: {}", cliente.getEntrenadorId(), cliente.getId(), e.getMessage());
            }
        }
        return dto;
    }

    public List<ClienteResponseDTO> obtenerClientes() {
        log.info("LISTANDO TODOS LOS CLIENTES");
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToDTOConEntrenador)
                .collect(Collectors.toList());
    }

    public Optional<ClienteResponseDTO> obtenerCliente(Long id) {
        log.info("BUSCANDO CLIENTE CON ID: {}", id);
        return clienteRepository.findById(id).map(c -> {
            log.info("CLIENTE CON ID {} ENCONTRADO", id);
            return mapToDTOConEntrenador(c);
        });
    }

    public ClienteResponseDTO saveCliente(ClienteRequestDTO dto) {
        log.info("GUARDANDO CLIENTE: {}", dto.getNombre());
        entrenadorClient.verificarEntrenadorExiste(dto.getEntrenadorId());

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setRun(dto.getRun());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setEntrenadorId(dto.getEntrenadorId());
        cliente.setEstablecimientoId(dto.getEstablecimientoId());

        Cliente guardado = clienteRepository.save(cliente);
        log.info("CLIENTE GUARDADO EXITOSAMENTE CON ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    public ClienteResponseDTO asignarEntrenadorInterno(Long clienteId, Long entrenadorId) {
        log.info("ASIGNANDO ENTRENADOR ID: {} AL CLIENTE ID: {} (LLAMADA INTERNA)", entrenadorId, clienteId);
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + clienteId + " no encontrado"));
        cliente.setEntrenadorId(entrenadorId);
        log.info("ENTRENADOR {} ASIGNADO AL CLIENTE {} EXITOSAMENTE", entrenadorId, clienteId);
        return mapToDTO(clienteRepository.save(cliente));
    }

    public void eliminarPorId(Long id) {
        log.info("ELIMINANDO CLIENTE CON ID: {}", id);
        clienteRepository.deleteById(id);
        log.info("CLIENTE CON ID {} ELIMINADO EXITOSAMENTE", id);
    }

    public List<ClienteResponseDTO> obtenerClientesPorEntrenador(Long entrenadorId) {
        log.info("BUSCANDO CLIENTES DEL ENTRENADOR CON ID: {}", entrenadorId);
        return clienteRepository.findByEntrenadorId(entrenadorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteResponseDTO> obtenerPorEstablecimiento(Long establecimientoId) {
        log.info("BUSCANDO CLIENTES DEL ESTABLECIMIENTO CON ID: {}", establecimientoId);
        return clienteRepository.findByEstablecimientoId(establecimientoId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
