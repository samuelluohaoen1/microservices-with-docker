package se.magnus.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    /*
    Objects needed to interact with our core microservices.
    `RestTemplate` is a synchronous web client.
    `ObjectMapper` provides functionality for reading and writing JSON.
     */
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;


    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.product-service.port}") String productServicePort,
            @Value("${app.recommendation-service.port}") String recommendationServicePort,
            @Value("${app.review-service.port}") String reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }


    /**
     * For the `getProduct()` implementation, the `getForObject()` method can be used in RestTemplate.
     * The expected response is a `Product` object, and it can be expressed in the call to `getForObject()`
     * by specifying the `Product.class` class that RestTemplate will map the JSON response to.
     *
     * @param productId The ID of the product in query as requested by the user.
     * @return An instance of the `Product` POJO.
     */
    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    /**
     * The `getRecommendations()` and `getReviews()` methods expect a generic list in the responses,
     * that is, `List<Recommendation>` and `List<Review>`. Since generics don't hold any type of
     * information at runtime, we can't specify that the methods expect a generic list in their
     * responses. Instead, we can use a helper class from the Spring Framework,`ParameterizedTypeReference`,
     * that is designed to resolve this problem by holding the type information at runtime.
     * This means that `RestTemplate` can figure out what class to map the JSON responses to.
     * To utilize this helper class, we have to use the more involved `exchange()` method instead of the
     * simpler `getForObject()` method on RestTemplate.
     *
     * @param productId The ID of the product in query as requested by the user.
     * @return A list of instances of the `Recommendation` POJO related to the requested product.
     */
    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + productId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
            }).getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        }catch (Exception ex){
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * The `getRecommendations()` and `getReviews()` methods expect a generic list in the responses,
     * that is, `List<Recommendation>` and `List<Review>`. Since generics don't hold any type of
     * information at runtime, we can't specify that the methods expect a generic list in their
     * responses. Instead, we can use a helper class from the Spring Framework,`ParameterizedTypeReference`,
     * that is designed to resolve this problem by holding the type information at runtime.
     * This means that `RestTemplate` can figure out what class to map the JSON responses to.
     * To utilize this helper class, we have to use the more involved `exchange()` method instead of the
     * simpler `getForObject()` method on RestTemplate.
     *
     * @param productId The ID of the product in query as requested by the user.
     * @return A list of instances of the `Review` POJO related to the requested product.
     */
    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;

            LOG.debug("Will call getReviews API on URL: {}", url);
            List<Review> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {}).getBody();

            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
}
