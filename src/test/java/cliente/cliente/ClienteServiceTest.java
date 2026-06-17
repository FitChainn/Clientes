package cliente.cliente;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import cliente.cliente.Service.ClienteService;
import cliente.cliente.WebClient.EntrenadorClient;
import cliente.cliente.dto.ClienteEntrenador;
import cliente.cliente.dto.ClienteRequestDTO;
import cliente.cliente.dto.ClienteResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE CLIENTE")
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EntrenadorClient entrenadorClient;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteRequestDTO cRequest;
    private ClienteEntrenador entrenadorSimple;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        cRequest = new ClienteRequestDTO("JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        entrenadorSimple = new ClienteEntrenador("CARLOS ENTRENADOR");
    }

    @Test
    @DisplayName("DEBE RETORNAR TODOS LOS CLIENTES")
    void shouldReturnTodosLosClientes() {
        Cliente cliente2 = new Cliente(2L, "MARIA LOPEZ", "11.111.111-1", LocalDate.of(1990, 3, 15), 2L, 3L);
        when(clienteRepository.findAll()).thenReturn(List.of(cliente, cliente2));
        when(entrenadorClient.obtenerEntrenadorSimple(2L)).thenReturn(entrenadorSimple);

        List<ClienteResponseDTO> result = clienteService.obtenerClientes();

        assertEquals(2, result.size());
        assertEquals("JUANITO PEREZ", result.get(0).getNombre());
        assertEquals("MARIA LOPEZ", result.get(1).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DEBE RETORNAR UN CLIENTE POR ID")
    void shouldReturnClienteById() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(entrenadorClient.obtenerEntrenadorSimple(2L)).thenReturn(entrenadorSimple);

        Optional<ClienteResponseDTO> result = clienteService.obtenerCliente(1L);

        assertTrue(result.isPresent());
        assertEquals("JUANITO PEREZ", result.get().getNombre());
        assertEquals("12.123.431-2", result.get().getRun());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI CLIENTE NO EXISTE POR ID")
    void shouldReturnEmptyWhenClienteNotFoundById() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ClienteResponseDTO> result = clienteService.obtenerCliente(99L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE GUARDAR UN CLIENTE")
    void shouldSaveCliente() {
        doNothing().when(entrenadorClient).verificarEntrenadorExiste(2L);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO result = clienteService.saveCliente(cRequest);

        assertNotNull(result);
        assertEquals("JUANITO PEREZ", result.getNombre());
        assertEquals("12.123.431-2", result.getRun());
        assertEquals(2L, result.getEntrenadorId());
        verify(entrenadorClient, times(1)).verificarEntrenadorExiste(2L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION SI ENTRENADOR NO EXISTE AL GUARDAR")
    void shouldThrowWhenEntrenadorNotFoundOnSave() {
        doThrow(new NoSuchElementException("El entrenador con ID 2 no existe"))
                .when(entrenadorClient).verificarEntrenadorExiste(2L);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> clienteService.saveCliente(cRequest));

        assertEquals("El entrenador con ID 2 no existe", ex.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("DEBE ASIGNAR ENTRENADOR INTERNO A UN CLIENTE")
    void shouldAsignarEntrenadorInterno() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO result = clienteService.asignarEntrenadorInterno(1L, 5L);

        assertNotNull(result);
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ASIGNAR ENTRENADOR A CLIENTE QUE NO EXISTE")
    void shouldThrowWhenAsignarEntrenadorClienteNotFound() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> clienteService.asignarEntrenadorInterno(99L, 5L));

        assertEquals("Cliente con id 99 no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN CLIENTE POR ID")
    void shouldEliminarClientePorId() {
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminarPorId(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR CLIENTES POR ENTRENADOR")
    void shouldReturnClientesByEntrenador() {
        when(clienteRepository.findByEntrenadorId(2L)).thenReturn(List.of(cliente));

        List<ClienteResponseDTO> result = clienteService.obtenerClientesPorEntrenador(2L);

        assertEquals(1, result.size());
        assertEquals("JUANITO PEREZ", result.get(0).getNombre());
        assertEquals(2L, result.get(0).getEntrenadorId());
        verify(clienteRepository, times(1)).findByEntrenadorId(2L);
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI ENTRENADOR NO TIENE CLIENTES")
    void shouldReturnEmptyListWhenEntrenadorHasNoClientes() {
        when(clienteRepository.findByEntrenadorId(99L)).thenReturn(List.of());

        List<ClienteResponseDTO> result = clienteService.obtenerClientesPorEntrenador(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE RETORNAR CLIENTES POR ESTABLECIMIENTO")
    void shouldReturnClientesByEstablecimiento() {
        when(clienteRepository.findByEstablecimientoId(3L)).thenReturn(List.of(cliente));

        List<ClienteResponseDTO> result = clienteService.obtenerPorEstablecimiento(3L);

        assertEquals(1, result.size());
        assertEquals("JUANITO PEREZ", result.get(0).getNombre());
        assertEquals(3L, result.get(0).getEstablecimientoId());
        verify(clienteRepository, times(1)).findByEstablecimientoId(3L);
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY CLIENTES EN ESE ESTABLECIMIENTO")
    void shouldReturnEmptyListWhenNoClientesEnEstablecimiento() {
        when(clienteRepository.findByEstablecimientoId(99L)).thenReturn(List.of());

        List<ClienteResponseDTO> result = clienteService.obtenerPorEstablecimiento(99L);

        assertTrue(result.isEmpty());
    }
}