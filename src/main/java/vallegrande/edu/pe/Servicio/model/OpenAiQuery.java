package vallegrande.edu.pe.Servicio.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table("open_ai_query") 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAiQuery {

    @Id
    private Long id;

    private String prompt;

    private String response;

    private LocalDateTime timestamp;

    public OpenAiQuery(String prompt, String response) {
        this.prompt = prompt;
        this.response = response;
        this.timestamp = LocalDateTime.now(); // Establece el timestamp al crear la instancia
    }
}
