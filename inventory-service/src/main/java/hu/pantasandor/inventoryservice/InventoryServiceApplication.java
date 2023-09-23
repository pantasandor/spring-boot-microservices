package hu.pantasandor.inventoryservice;

import hu.pantasandor.inventoryservice.model.Inventory;
import hu.pantasandor.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            var inventories = inventoryRepository.findAll();
            if (!inventories.isEmpty()) {
                return;
            }

            var inventory1 = new Inventory();
            inventory1.setSkuCode("AAA111");
            inventory1.setQuantity(1);

            var inventory2 = new Inventory();
            inventory2.setSkuCode("BBB222");
            inventory2.setQuantity(0);

            var inventory3 = new Inventory();
            inventory3.setSkuCode("CCC333");
            inventory3.setQuantity(6);

            inventoryRepository.saveAll(List.of(inventory1, inventory2, inventory3));
        };
    }

}
