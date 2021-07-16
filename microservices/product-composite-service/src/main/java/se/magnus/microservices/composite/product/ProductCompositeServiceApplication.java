package se.magnus.microservices.composite.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static java.util.Collections.emptyList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;


@SpringBootApplication
@ComponentScan("se.magnus")
@EnableSwagger2
public class ProductCompositeServiceApplication {


    @Value("${api.common.version}")             String apiVersion;
    @Value("${api.common.title}")               String apiTitle;
    @Value("${api.common.description}")         String apiDescription;
    @Value("${api.common.termsOfServiceUrl}")   String apiTermsOfServiceUrl;
    @Value("${api.common.license}")             String apiLicense;
    @Value("${api.common.licenseUrl}")          String apiLicenseUrl;
    @Value("${api.common.contact.name}")        String apiContactName;
    @Value("${api.common.contact.url}")         String apiContactUrl;
    @Value("${api.common.contact.email}")       String apiContactEmail;


    /**
     * Will exposed on $HOST:$PORT/swagger-ui.html
     * SpringFox configuration bean.
     *
     * @return
     */
    @Bean
    public Docket apiDocumentation(){
        return new Docket(SWAGGER_2)    // Use Swagger V2 documentation
                .select()
                .apis(basePackage("se.magnus.microservices.composite.product")) // Tell SpringFox to look for API documentations in this package
                .paths(PathSelectors.any()) // Any is valid
                .build()    // Build the Docket
                .globalResponses(HttpMethod.GET, emptyList())   //we ask SpringFox not to add any default HTTP response codes to the API documentation
                .apiInfo(new ApiInfo(
                        apiTitle,
                        apiDescription,
                        apiVersion,
                        apiTermsOfServiceUrl,
                        new Contact(apiContactName, apiContactUrl, apiContactEmail),
                        apiLicense,
                        apiLicenseUrl,
                        emptyList()
                ));
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }

    /**
     * Configuration of a web client used to perform the actual HTTP requests to
     * our core microservices. Remember that any configuration done here is
     * equivalent to writing a `@Configuration` class because `@SpringBootApplication`
     * is the combination of `@Configuration`, `@ComponentScan`,
     * and `@EnableAutoConfiguration`.
     */
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
