package hu.pantasandor.productservice.controller;

import hu.pantasandor.productservice.dto.ProductRequest;
import hu.pantasandor.productservice.dto.ProductResponse;
import hu.pantasandor.productservice.mapper.ProductMapper;
import hu.pantasandor.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static hu.pantasandor.productservice.controller.ProductController.API_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(API_URL)
@RequiredArgsConstructor
public class ProductController {

    public static final String API_URL = "/api/v1/product";

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ProductResponse createProduct(@RequestBody @Valid ProductRequest productRequest) {
        String name = productRequest.getName();
        String description = productRequest.getDescription();
        BigDecimal price = productRequest.getPrice();
        return ProductMapper.INSTANCE.doMap(productService.createProduct(name, description, price));
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductMapper.INSTANCE::doMap)
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ProductResponse getProduct(@PathVariable String id) {
        return ProductMapper.INSTANCE.doMap(productService.getProduct(id));
    }

}
