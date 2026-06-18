package cliente.cliente;

import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE CLIENTE")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private TestEntityManager em;

    private Cliente crearCliente(String nombre, String run, Long entrenadorId, Long establecimientoId) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setRun(run);
        c.setFechaNacimiento(LocalDate.of(1995, 5, 10));
        c.setEntrenadorId(entrenadorId);
        c.setEstablecimientoId(establecimientoId);
        return em.persistAndFlush(c);
    }

    @Test
    @DisplayName("DEBE ENCONTRAR UN CLIENTE POR ID")
    void findById_ShouldReturnCliente() {
        Cliente c = crearCliente("JUANITO PEREZ", "12.123.431-2", 1L, 2L);

        Optional<Cliente> result = repo.findById(c.getId());

        assertTrue(result.isPresent());
        assertEquals("JUANITO PEREZ", result.get().getNombre());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI CLIENTE NO EXISTE")
    void findById_ShouldReturnEmpty() {
        Optional<Cliente> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR CLIENTES POR ENTRENADOR ID")
    void findByEntrenadorId_ShouldReturnClientes() {
        crearCliente("JUANITO PEREZ", "12.123.431-2", 1L, 2L);
        crearCliente("MARIA LOPEZ", "11.111.111-1", 1L, 2L);
        crearCliente("PEDRO SOTO", "22.222.222-2", 2L, 3L);

        List<Cliente> result = repo.findByEntrenadorId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI ENTRENADOR NO TIENE CLIENTES")
    void findByEntrenadorId_ShouldReturnEmpty() {
        List<Cliente> result = repo.findByEntrenadorId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR CLIENTES POR ESTABLECIMIENTO ID")
    void findByEstablecimientoId_ShouldReturnClientes() {
        crearCliente("JUANITO PEREZ", "12.123.431-2", 1L, 5L);
        crearCliente("MARIA LOPEZ", "11.111.111-1", 2L, 5L);
        crearCliente("PEDRO SOTO", "22.222.222-2", 3L, 6L);

        List<Cliente> result = repo.findByEstablecimientoId(5L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getEstablecimientoId().equals(5L)));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY CLIENTES EN ESE ESTABLECIMIENTO")
    void findByEstablecimientoId_ShouldReturnEmpty() {
        List<Cliente> result = repo.findByEstablecimientoId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE GUARDAR UN CLIENTE")
    void save_ShouldPersistCliente() {
        Cliente c = new Cliente();
        c.setNombre("JUANITO PEREZ");
        c.setRun("12.123.431-2");
        c.setFechaNacimiento(LocalDate.of(1995, 5, 10));
        c.setEntrenadorId(1L);
        c.setEstablecimientoId(2L);

        Cliente saved = repo.save(c);

        assertNotNull(saved.getId());
        assertEquals("JUANITO PEREZ", saved.getNombre());
        assertEquals("12.123.431-2", saved.getRun());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN CLIENTE")
    void delete_ShouldRemoveCliente() {
        Cliente c = crearCliente("JUANITO PEREZ", "12.123.431-2", 1L, 2L);
        Long id = c.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}