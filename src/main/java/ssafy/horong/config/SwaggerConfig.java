package ssafy.horong.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Configuration
@Slf4j
@Profile("!test")
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(ServletContext servletContext){
        log.debug(">>> [SwaggerConfig::openAPI] OpenAPI 설정");
        String contextPath = servletContext.getContextPath();
        Server server = new Server().url(contextPath);
        return new OpenAPI().servers(List.of(server))
                .info(info())
                .addSecurityItem(securityItem())
                .components(new Components()
                        .addSecuritySchemes("Authorization(oauthAccessToken)", securityScheme()));
    }

    private Info info() {
        return new Info()
                .title("horong Client API")
                .version("v1")
                .description("SSAFY A108 horong Client API Document")
                .license(license());
    }

    private License license() {
        return new License()
                .url("https://lab.ssafy.com/s11-final/S11P31A108")
                .name("A108");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);
    }

    private SecurityRequirement securityItem() {
        return new SecurityRequirement()
                .addList("Authorization(oauthAccessToken)");
    }
}
