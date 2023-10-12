package hu.pantasandor.productservice;

import hu.pantasandor.productservice.dto.ProductRequest;
import hu.pantasandor.productservice.dto.ProductResponse;
import hu.pantasandor.productservice.model.Product;
import hu.pantasandor.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.List;

import static hu.pantasandor.productservice.controller.ProductController.API_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProductServiceApplicationTests {

    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_DESCRIPTION = "product_desc";
    public static final String INVALID_PRODUCT_ID = "invalid_product_id";
    public static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1_000_000);

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    private final ProductRepository productRepository;
    private final WebTestClient webClient;

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        dynamicPropertyRegistry.add("spring.cloud.discovery.enabled", () -> false);
    }

    @BeforeEach
    private void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Should not get products")
    public void shouldNotGetProducts() {
        assertTrue(productRepository.findAll().isEmpty());

        webClient.get()
                .uri(API_URL)
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBody(ProductResponse[].class)
                .isEqualTo(new ProductResponse[]{});
    }

    @Test
    @DisplayName("Should not create product with empty name")
    public void shouldNotCreateProductWithEmptyName() {
        var productRequest = getProductRequest(null, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
        webClient.post()
                .uri(API_URL)
                .bodyValue(productRequest)
                .exchange()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectStatus().isBadRequest();

        assertTrue(productRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should create product")
    public void shouldCreateProduct() {
        var productRequest = getProductRequest(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
        webClient.post()
                .uri(API_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .value(ProductResponse::getId, notNullValue())
                .value(ProductResponse::getName, equalTo(PRODUCT_NAME))
                .value(ProductResponse::getDescription, equalTo(PRODUCT_DESCRIPTION))
                .value(ProductResponse::getPrice, equalTo(PRODUCT_PRICE));

        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @DisplayName("Should get products")
    public void shouldGetProducts() {
        productRepository.save(getProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE));
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());

        var id = products.get(0).getId();

        webClient.get()
                .uri(API_URL)
                .exchange()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .value(List::size, equalTo(1))
                .value(responses -> responses, contains(getProductResponse(id, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE)));
    }

    @Test
    @DisplayName("Should get product")
    public void shouldGetProduct() {
        productRepository.save(getProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE));
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());

        var id = products.get(0).getId();
        webClient.get()
                .uri(API_URL + "/" + id)
                .exchange()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(ProductResponse::getId, equalTo(id))
                .value(ProductResponse::getName, equalTo(PRODUCT_NAME))
                .value(ProductResponse::getDescription, equalTo(PRODUCT_DESCRIPTION))
                .value(ProductResponse::getPrice, equalTo(PRODUCT_PRICE));
    }

    @Test
    @DisplayName("Should not get product")
    public void shouldNotGetProduct() {
        assertEquals(0, productRepository.findAll().size());

        webClient.get()
                .uri(API_URL + "/" + INVALID_PRODUCT_ID)
                .exchange()
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectStatus().isNotFound();
    }

    private ProductResponse getProductResponse(String id, String name, String description, BigDecimal price) {
        return ProductResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .build();
    }

    private ProductRequest getProductRequest(String name, String description, BigDecimal price) {
        return ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }

    private Product getProduct(String name, String description, BigDecimal price) {
        var product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);

        return product;
    }

}
