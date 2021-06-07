package se.magnus.api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * We define an interface for our API. Any class that implements this interface
 * would automatically have a `getProduct` method which is mapped to the RESTful
 * url "/product/{productId}".
 */
public interface ProductService {

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId The ID of the product in query as requested by the user.
     * @return the product, if found, else null
     */
    @GetMapping(
        value    = "/product/{productId}",
        produces = "application/json")
     Product getProduct(@PathVariable int productId);
}
