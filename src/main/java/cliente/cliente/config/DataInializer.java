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

        clienteRepository.save(new Cliente(null, "Juan Pérez", "12345678-k", LocalDate.parse("1990-01-01")));
        clienteRepository.save(new Cliente(null, "María García", "9876543-2", LocalDate.parse("1985-11-15")));
        clienteRepository.save(new Cliente(null, "Carlos Soto", "15432987-1", LocalDate.parse("1992-03-20")));
        clienteRepository.save(new Cliente(null, "Ana Morales", "11222333-4", LocalDate.parse("1988-07-05")));
        clienteRepository.save(new Cliente(null, "Diego Rojas", "18555444-9", LocalDate.parse("2000-12-30")));
        clienteRepository.save(new Cliente(null, "Elena Silva", "14666777-2", LocalDate.parse("1994-02-14")));
        clienteRepository.save(new Cliente(null, "Roberto Díaz", "10111222-3", LocalDate.parse("1980-06-18")));
        clienteRepository.save(new Cliente(null, "Patricia López", "17888999-k", LocalDate.parse("1998-09-22")));
        clienteRepository.save(new Cliente(null, "Fernando Tapia", "13333444-1", LocalDate.parse("1982-10-10")));
        clienteRepository.save(new Cliente(null, "Sofía Castro", "16777888-5", LocalDate.parse("1996-04-05")));
    }





}
