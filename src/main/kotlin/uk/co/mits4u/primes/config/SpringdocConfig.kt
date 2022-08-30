package uk.co.mits4u.primes.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringdocConfig {

    @Bean
    fun springShopOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Primes API")
                    .description("Primes API documentation")
                    .version("v1.0.0")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("TotFin Wiki Documentation")
                    .url("https://github.com/veretie/primes-api-java")
            )
    }

}