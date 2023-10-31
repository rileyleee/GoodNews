package com.ssafy.goodnews.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig()
                .addAnnotationsToIgnore(AuthenticationPrincipal.class);
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("member")
                .pathsToMatch("/api/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi familyApi() {
        return GroupedOpenApi.builder()
                .group("family")
                .pathsToMatch("/api/family/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mapApi() {
        return GroupedOpenApi.builder()
                .group("map")
                .pathsToMatch("/api/map/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Parameter authHeader = new Parameter()
                .in("header")
                .name("Authorization")
                .description("Autorhization header fort he API")
                .required(true)
                .schema(new StringSchema());
        return new OpenAPI()
                .components(new Components().addParameters("Authorization", authHeader))
                .info(new Info().title("API 명세서")
                        .description("희소식 API 명세서")
                        .version("v0.0.1"));
    }
}
