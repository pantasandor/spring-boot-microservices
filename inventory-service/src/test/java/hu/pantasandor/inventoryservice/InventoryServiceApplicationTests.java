package hu.pantasandor.inventoryservice;

import hu.pantasandor.inventoryservice.dto.InventoryResponse;
import hu.pantasandor.inventoryservice.model.Inventory;
import hu.pantasandor.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;
import org.testcontainers.containers.MySQLContainer;

import java.net.URI;
import java.util.List;

import static hu.pantasandor.inventoryservice.controller.InventoryController.API_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryServiceApplicationTests {

    public static final String VALID_SKU_CODE = "valid_sku_code";
    public static final String VALID_SKU_CODE_2 = "valid_sku_code_2";
    public static final String INVALID_SKU_CODE = "invalid_sku_code";
    public static final String SKU_CODE_PARAM = "skuCode";

    public static final Integer VALID_QUANTITY = 1;

    public static final Integer INVALID_QUANTITY = 0;


    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    private final InventoryRepository inventoryRepository;
    private final WebTestClient webClient;

    @BeforeAll
    private static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    private static void afterAll() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    private void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should not get stock result for empty skuCodes")
    public void shouldNotGetStockResultForEmptySkuCodes() {
        assertTrue(inventoryRepository.findAll().isEmpty());

        webClient.get()
                .uri(builder -> buildUri(builder, API_URL))
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Should get stock result for empty quantity")
    public void shouldGetStockResultForEmptyQuantity() {
        inventoryRepository.save(getInventory(VALID_SKU_CODE, INVALID_QUANTITY));
        assertEquals(1, inventoryRepository.findAll().size());

        webClient.get()
                .uri(builder -> buildUri(builder, API_URL, VALID_SKU_CODE))
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBodyList(InventoryResponse.class)
                .value(List::size, equalTo(1))
                .value(responses -> responses, contains(getInventoryResponse(VALID_SKU_CODE, false)));
    }

    @Test
    @DisplayName("Should get stock result for not empty quantity")
    public void shouldGetStockResultForNotEmptyQuantity() {
        inventoryRepository.save(getInventory(VALID_SKU_CODE, VALID_QUANTITY));
        assertEquals(1, inventoryRepository.findAll().size());

        webClient.get()
                .uri(builder -> buildUri(builder, API_URL, VALID_SKU_CODE))
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBodyList(InventoryResponse.class)
                .value(List::size, equalTo(1))
                .value(responses -> responses, contains(getInventoryResponse(VALID_SKU_CODE, true)));
    }

    @Test
    @DisplayName("Should not get stock result for invalid skuCode")
    public void shouldNotGetStockResultForInvalidSkuCode() {
        inventoryRepository.save(getInventory(VALID_SKU_CODE, VALID_QUANTITY));
        assertEquals(1, inventoryRepository.findAll().size());

        webClient.get()
                .uri(builder -> buildUri(builder, API_URL, INVALID_SKU_CODE))
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBody(InventoryResponse[].class)
                .isEqualTo(new InventoryResponse[]{});
    }

    @Test
    @DisplayName("Should get multiple stock result")
    public void shouldGetMultipleStockResult() {
        List<Inventory> inventories = List.of(
                getInventory(VALID_SKU_CODE, VALID_QUANTITY),
                getInventory(VALID_SKU_CODE_2, VALID_QUANTITY)
        );
        inventoryRepository.saveAll(inventories);
        assertEquals(inventories.size(), inventoryRepository.findAll().size());

        webClient.get()
                .uri(builder -> buildUri(builder, API_URL, VALID_SKU_CODE, VALID_SKU_CODE_2))
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBodyList(InventoryResponse.class)
                .value(List::size, equalTo(inventories.size()))
                .value(responses -> responses, containsInAnyOrder(
                        getInventoryResponse(VALID_SKU_CODE, true),
                        getInventoryResponse(VALID_SKU_CODE_2, true)
                ));
    }

    private static URI buildUri(UriBuilder builder, String url, String... skuCodes) {
        builder.path(url);
        if (skuCodes != null && skuCodes.length > 0) {
            builder.queryParam(SKU_CODE_PARAM, skuCodes);
        }

        return builder.build();
    }

    private InventoryResponse getInventoryResponse(String skuCode, boolean isInStock) {
        return InventoryResponse.builder().skuCode(skuCode).isInStock(isInStock).build();
    }

    private Inventory getInventory(String skuCode, Integer quantity) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(quantity);

        return inventory;
    }

}
