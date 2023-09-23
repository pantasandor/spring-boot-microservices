package hu.pantasandor.productservice;

import hu.pantasandor.productservice.dto.ProductRequest;
import hu.pantasandor.productservice.dto.ProductResponse;
import hu.pantasandor.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProductServiceApplicationTests {

    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_DESCRIPTION = "product_desc";
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

    @Test
    @Order(0)
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
    @Order(1)
    @DisplayName("Should not create product with empty name")
    public void shouldNotCreateProductWithEmptyName() {
        var responseEntity = getProductRequest(null, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
        webClient.post()
                .uri(API_URL)
                .bodyValue(responseEntity)
                .exchange()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectStatus().isBadRequest();

        assertTrue(productRepository.findAll().isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Should create product")
    public void shouldCreateProduct() {
        var responseEntity = getProductRequest(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
        webClient.post()
                .uri(API_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(responseEntity)
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
    @Order(3)
    @DisplayName("Should get products")
    public void shouldGetProducts() {
        assertEquals(1, productRepository.findAll().size());

        webClient.get()
                .uri(API_URL)
                .exchange()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .value(List::size, equalTo(1))
                .value(responses -> responses.get(0).getName(), equalTo(PRODUCT_NAME))
                .value(responses -> responses.get(0).getDescription(), equalTo(PRODUCT_DESCRIPTION))
                .value(responses -> responses.get(0).getPrice(), equalTo(PRODUCT_PRICE));
    }

    @Test
    @Order(4)
    @DisplayName("Should get product")
    public void shouldGetProduct() {
        var id = productRepository.findAll().get(0).getId();
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

    private ProductRequest getProductRequest(String name, String description, BigDecimal price) {
        return ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }

}
