package vallegrande.edu.pe.Servicio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Servicio.model.OpenAiQuery;
import vallegrande.edu.pe.Servicio.repository.OpenAiQueryRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AzureOpenAiService {

    private final WebClient.Builder webClientBuilder;
    private final OpenAiQueryRepository repository;

    private static final String AZURE_OPENAI_API_URL = "https://gpt-cris2024.openai.azure.com/openai/deployments/gpt-Cristhoper-4o/chat/completions?api-version=2023-03-15-preview";
    private static final String AZURE_API_KEY = "a2e809a7d9fd4eb386b70bd06831caee";

    /**
     * Método para obtener la respuesta de Azure OpenAI y guardar solo el contenido de texto
     */
    public Mono<OpenAiQuery> getOpenAiResponse(String prompt) {
        WebClient webClient = webClientBuilder.build();

        // Crear el cuerpo de la solicitud (request body) para el API
        Map<String, Object> body = Map.of(
                "messages", new Object[] {
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", prompt)
                },
                "max_tokens", 1000,
                "temperature", 0.7
        );

        return webClient.post()
                .uri(AZURE_OPENAI_API_URL)
                .header("api-key", AZURE_API_KEY)  // Autenticación
                .bodyValue(body)                   // Enviar el cuerpo de la solicitud
                .retrieve()
                .bodyToMono(String.class)          // Convertir la respuesta en String
                .flatMap(responseJson -> {
                    // Extraer solo el contenido de la respuesta del assistant
                    String content = extractContentFromResponse(responseJson);

                    OpenAiQuery query = OpenAiQuery.builder()
                            .prompt(prompt)
                            .response(content) // Guardar solo el contenido del mensaje
                            .timestamp(LocalDateTime.now())
                            .build();
                    return repository.save(query);  // Guardar en base de datos
                })
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.error(new RuntimeException("Error en la solicitud a Azure OpenAI: " + ex.getResponseBodyAsString(), ex))
                );
    }

    /**
     * Método para actualizar la consulta y almacenar solo el contenido de texto
     */
    public Mono<OpenAiQuery> updateQuery(Long id, String newPrompt) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Query not found with id " + id)))
                .flatMap(existingQuery -> {
                    // Actualizar el prompt y la fecha de modificación
                    existingQuery.setPrompt(newPrompt);
                    existingQuery.setTimestamp(LocalDateTime.now());

                    // Crear el cuerpo de la solicitud (request body) para el API
                    Map<String, Object> body = Map.of(
                            "messages", new Object[]{
                                    Map.of("role", "system", "content", "You are a helpful assistant."),
                                    Map.of("role", "user", "content", newPrompt)
                            },
                            "max_tokens", 1000,
                            "temperature", 0.7
                    );

                    WebClient webClient = webClientBuilder.build();

                    return webClient.post()
                            .uri(AZURE_OPENAI_API_URL)
                            .header("api-key", AZURE_API_KEY)  // Autenticación
                            .bodyValue(body)                   // Enviar el cuerpo de la solicitud
                            .retrieve()
                            .bodyToMono(String.class)          // Convertir la respuesta en String
                            .flatMap(responseJson -> {
                                String content = extractContentFromResponse(responseJson);
                                existingQuery.setResponse(content);
                                return repository.save(existingQuery);
                            });
                });
    }

    /**
     * Método para eliminar una consulta por ID
     */
    public Mono<Void> deleteQuery(Long id) {
        return repository.deleteById(id);
    }

    /**
     * Método para crear una nueva consulta y guardar solo el texto
     */
    public Mono<OpenAiQuery> createQuery(String prompt) {
        return getOpenAiResponse(prompt);
    }

    /**
     * Método para obtener todas las consultas
     */
    public Flux<OpenAiQuery> getAllQueries() {
        return repository.findAll();
    }

    /**
     * Método auxiliar para extraer el contenido del mensaje de la respuesta JSON
     */
    private String extractContentFromResponse(String responseJson) {
        try {
            // Parsear el JSON y extraer el campo "content"
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseJson);
            return rootNode.path("choices").get(0).path("message").path("content").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al procesar la respuesta de OpenAI", e);
        }
    }
}
