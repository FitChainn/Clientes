package cliente.cliente.config;


import cliente.cliente.Modelo.Cliente;
import cliente.cliente.Repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    @Override
    public void run (String... args){
        if(clienteRepository.count()>0){
            log.info("Data inializer: La BD ya tiene datos, se omite la carga de datos");
            return;
        }

        log.info("Data inializer: Base de datos vacia, llenando datos");

        clienteRepository.save(new Cliente(null, "Juan Pérez", "12345678-k", LocalDate.of(1990, 12,15),null));
        clienteRepository.save(new Cliente(null, "María García", "9876543-2", LocalDate.of(1985,11,15),null));
        clienteRepository.save(new Cliente(null, "Carlos Soto", "15432987-1", LocalDate.of(1992,3,20),null));
        clienteRepository.save(new Cliente(null, "Ana Morales", "11222333-4", LocalDate.of(1988,7,5),null));
        clienteRepository.save(new Cliente(null, "Diego Rojas", "18555444-9", LocalDate.of(2000,12,30),null));
        clienteRepository.save(new Cliente(null, "Elena Silva", "14666777-2", LocalDate.of(1994,2,14),null));
        clienteRepository.save(new Cliente(null, "Roberto Díaz", "10111222-3", LocalDate.of(1980,6,18),null));
        clienteRepository.save(new Cliente(null, "Patricia López", "17888999-k", LocalDate.of(1998,9,22),null));
        clienteRepository.save(new Cliente(null, "Fernando Tapia", "13333444-1", LocalDate.of(1982,10,10),null));
        clienteRepository.save(new Cliente(null, "Sofía Castro", "16777888-5", LocalDate.of(1996,4,5),null));
    }
}
