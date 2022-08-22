package com.enerdeal.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket enerdealApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()

                .apis(RequestHandlerSelectors.basePackage("com.enerdeal"))
                .paths(regex("/*.*")).build()
                .apiInfo(metaData())
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title("Rensource Energy HQ")
                .description("Enerdeal Application").version("1.0.0")
                .license("Apache License Version 2.0").licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Rensource Energy HQ", "www.xxxxxxx.com ", "info@xxxxxx.com")).build();
    }

    private ApiKey apiKey(){
        return new ApiKey("JWT", "Authorization", "Header");
    }

    private SecurityContext securityContext(){
        return SecurityContext.builder().securityReferences(defaultAuthSecurityReferences()).build();
    }

    private List<SecurityReference> defaultAuthSecurityReferences(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global","have access to all the endpoints");
        AuthorizationScope [] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT",authorizationScopes));
    }







}

