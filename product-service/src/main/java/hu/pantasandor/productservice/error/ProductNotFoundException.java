package hu.pantasandor.productservice.error;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(String id) {
        super(NOT_FOUND, "/product/product-not-found", "Product with id " + id + " not found");
    }
}


