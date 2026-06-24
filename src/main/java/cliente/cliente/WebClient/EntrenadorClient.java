package cliente.cliente.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntrenadorClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${entrenador.service.url}")
    private String entrenadorServiceUrl;

    public void verificarEntrenadorExiste(Long entrenadorId) {
        log.info("Verificando que existe entrenador con id {}", entrenadorId);
        try {
            webClientBuilder.build()
                    .get()
                    .uri(entrenadorServiceUrl + "/v1/entrenadores/" + entrenadorId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Entrenador con id {} no encontrado", entrenadorId);
            throw new NoSuchElementException("El entrenador con ID " + entrenadorId + " no existe");
        } catch (Exception e) {
            log.error("Error al conectar con microservicio Entrenador: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de entrenadores");
        }
    }

    public Object obtenerEntrenadorSimple(Long entrenadorId) {
        log.info("Obteniendo datos simples del entrenador {}", entrenadorId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(entrenadorServiceUrl + "/v1/entrenadores/" + entrenadorId + "/simple")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            log.warn("No se pudo obtener entrenador con id {}", entrenadorId);
            return null;
        }
    }
}