package se.magnus.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@ComponentScan("se.magnus")
public class ProductCompositeServiceApplication {

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
