package vallegrande.edu.pe.Servicio.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Servicio.model.OpenAiQuery;
import vallegrande.edu.pe.Servicio.service.AzureOpenAiService;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
@Tag(name = "Consultas OpenAI", description = "Puntos finales para interactuar con OpenAI")
public class AzureOpenAiRest {

    private final AzureOpenAiService azureOpenAiService;

    @Operation(summary = "Obtener respuesta de OpenAI", description = "Obtener una respuesta de OpenAI para un prompt dado")
    @ApiResponse(responseCode = "200", description = "Respuesta obtenida con éxito")
    @ApiResponse(responseCode = "400", description = "Prompt inválido o error en la solicitud")
    @GetMapping
    public Mono<ResponseEntity<OpenAiQuery>> getOpenAiResponse(
            @Parameter(description = "Prompt para OpenAI") @RequestParam String prompt) {
        return azureOpenAiService.getOpenAiResponse(prompt)
                .map(query -> ResponseEntity.ok(query))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build())); // Cambié a onErrorResume
    }

    @Operation(summary = "Actualizar una consulta de OpenAI", description = "Actualizar el prompt de una consulta existente de OpenAI por ID")
    @ApiResponse(responseCode = "200", description = "Consulta actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OpenAiQuery>> updateQuery(
            @Parameter(description = "ID de la consulta a actualizar") @PathVariable Long id,
            @Parameter(description = "Nuevo prompt para la consulta") @RequestParam String newPrompt) {
        return azureOpenAiService.updateQuery(id, newPrompt)
                .map(updatedQuery -> ResponseEntity.ok(updatedQuery))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build())); // Cambié a onErrorResume
    }

    @Operation(summary = "Eliminar una consulta de OpenAI", description = "Eliminar una consulta existente de OpenAI por ID")
    @ApiResponse(responseCode = "200", description = "Consulta eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteQuery(
            @Parameter(description = "ID de la consulta a eliminar") @PathVariable Long id) {
        return azureOpenAiService.deleteQuery(id)
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build())); // Cambié a onErrorResume
    }

    @Operation(summary = "Crear una nueva consulta de OpenAI", description = "Crear una nueva consulta de OpenAI con el prompt dado")
    @ApiResponse(responseCode = "201", description = "Consulta creada con éxito")
    @ApiResponse(responseCode = "400", description = "Prompt inválido o error en la solicitud")
    @PostMapping
    public Mono<ResponseEntity<OpenAiQuery>> createQuery(
            @Parameter(description = "Prompt para la nueva consulta de OpenAI") @RequestParam String prompt) {
        return azureOpenAiService.createQuery(prompt)
                .map(createdQuery -> ResponseEntity.status(HttpStatus.CREATED).body(createdQuery))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build())); // Cambié a onErrorResume
    }

    @Operation(summary = "Listar todas las consultas de OpenAI", description = "Recuperar todas las consultas de OpenAI almacenadas en la base de datos")
    @ApiResponse(responseCode = "200", description = "Consultas recuperadas con éxito")
    @GetMapping("/all")
    public Flux<OpenAiQuery> listAllQueries() {
        return azureOpenAiService.getAllQueries();
    }
}
