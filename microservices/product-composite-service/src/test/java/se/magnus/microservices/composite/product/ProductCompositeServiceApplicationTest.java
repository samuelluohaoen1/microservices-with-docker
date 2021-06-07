package se.magnus.microservices.composite.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.microservices.composite.product.services.ProductCompositeIntegration;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/*
When running tests annotated with `@SpringBootTest`
the corresponding microservice under test will be
started automatically
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
public class ProductCompositeServiceApplicationTest {


    /*
    Note the use of constants here. This allows us to be constant in both
    our mocks and our tests. Also improving readability.
     */
    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_INVALID = 3;
    private static final int PRODUCT_ID_NOT_FOUND = 2;


    @Autowired
    private WebTestClient client;


    /**
     * Since our `product-composite-service` makes requests to other microservices, we can't
     * possibly start them all up just for this test. Therefore, we use `mockito`, a bean
     * mocking API provided by Spring Boot. By annotating beans with `@MockBean`, we can
     * define the mock API for our beans.
     */
    @MockBean
    private ProductCompositeIntegration compositeIntegration;

    @Before
    public void setUp() {
        when(compositeIntegration.getProduct(PRODUCT_ID_OK))
                .thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));

        when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
                .thenReturn(Collections.singletonList(
                        new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));

        when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

        when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
        when(compositeIntegration.getReviews(PRODUCT_ID_OK))
                .thenReturn(Collections.singletonList(
                        new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));
    }


    @Test
    public void getProductById() {
        client.get()
                .uri("/product-composite/" + PRODUCT_ID_OK)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
                .jsonPath("$.recommendations.length()").isEqualTo(1)
                .jsonPath("$.reviews.length()").isEqualTo(1);
    }

    @Test
    public void getProductNotFound() {
        client.get()
                .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
    }
}