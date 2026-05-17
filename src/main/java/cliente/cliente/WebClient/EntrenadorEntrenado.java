package cliente.cliente.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class EntrenadorEntrenado {private final WebClient webClient;

    public EntrenadorEntrenado(@Value("${cliente-service.url}")String clienteServidor){
        this.webClient = WebClient.builder().baseUrl(clienteServidor).build();
    }

    //metodo para poder comunicarme con el microservicio
    //cliente y verificar si el cliente existe
    //devolver una estructura de mapa que representa el json
    public Map<String, Object> obtenerClienteId(Long id){
        //realizar una consulta HTTP de tipo get al microservicio cliente
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Cliente no encontrado")))
                .bodyToMono(Map.class).block();

    }
}

