package hu.pantasandor.inventoryservice.service;

import hu.pantasandor.inventoryservice.model.Inventory;
import hu.pantasandor.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<Inventory> isInStock(List<String> skuCodes) {
        log.info("isInStock skuCodes: {}", skuCodes);
        var inventories = inventoryRepository.findBySkuCodeIn(skuCodes);
        log.info("isInStock inventories: {}", inventories);
        return inventories;
    }

}
