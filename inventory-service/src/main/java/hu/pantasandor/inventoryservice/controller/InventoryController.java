package hu.pantasandor.inventoryservice.controller;

import hu.pantasandor.inventoryservice.dto.InventoryResponse;
import hu.pantasandor.inventoryservice.mapper.InventoryMapper;
import hu.pantasandor.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.pantasandor.inventoryservice.controller.InventoryController.API_URL;

@RestController
@RequestMapping(API_URL)
@RequiredArgsConstructor
public class InventoryController {

    public static final String API_URL = "/api/v1/inventory";

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam("skuCode") List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes).stream()
                .map(InventoryMapper.INSTANCE::doMap)
                .toList();
    }

}
