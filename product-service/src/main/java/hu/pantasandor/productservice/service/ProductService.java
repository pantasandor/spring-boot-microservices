package hu.pantasandor.productservice.service;

import hu.pantasandor.productservice.model.Product;
import hu.pantasandor.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(String name, String description, BigDecimal price) {
        var product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
        product = productRepository.save(product);
        log.info("createProduct: {}", product);

        return product;
    }

    public List<Product> getAllProducts() {
        var products = productRepository.findAll();
        log.info("getAllProducts: {}", products);

        return products;
    }

    public Product getProduct(String id) {
        var product = productRepository.findById(id).orElseThrow();
        log.info("getProduct: {} {}", id, product);

        return product;
    }

}
