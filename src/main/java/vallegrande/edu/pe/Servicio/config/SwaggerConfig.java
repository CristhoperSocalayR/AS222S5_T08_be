package vallegrande.edu.pe.Servicio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class SwaggerConfig implements WebFluxConfigurer {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://expert-train-wp6v4v999vphwgp-8085.app.github.dev/"))
                .info(new Info()
                        .title("Oracle ATP Rest API")
                        .description("Especificación de REST API services")
                        .license(new License().name("Valle Grande").url("https://vallegrande.edu.pe"))
                        .version("1.0.0")
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuración de CORS más segura
        registry.addMapping("/**")  // Permite todas las rutas
                .allowedOrigins("https://turbo-broccoli-76q7x7rr977crgg5-4200.app.github.dev")  // Ajusta el origen a tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Métodos permitidos
                .allowedHeaders("*")  // Permitir todos los headers
                .allowCredentials(true);  // Permitir cookies si es necesario
    }
}
