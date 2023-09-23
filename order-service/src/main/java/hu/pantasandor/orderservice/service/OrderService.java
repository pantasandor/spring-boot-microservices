package hu.pantasandor.orderservice.service;

import hu.pantasandor.orderservice.dto.InventoryResponse;
import hu.pantasandor.orderservice.dto.OrderLineItemDto;
import hu.pantasandor.orderservice.dto.OrderRequest;
import hu.pantasandor.orderservice.exception.OrderException;
import hu.pantasandor.orderservice.mapper.OrderLineItemMapper;
import hu.pantasandor.orderservice.model.Order;
import hu.pantasandor.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) throws OrderException {
        var orderLineItemDtoList = orderRequest.getOrderLineItemDtoList();
        var skuCodes = orderLineItemDtoList.stream()
                .map(OrderLineItemDto::getSkuCode)
                .toList();

        var inventoryResponses = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/v1/inventory", ub -> ub.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        var allProductsInStock = inventoryResponses !=null && inventoryResponses.length == orderLineItemDtoList.size() &&
                Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            throw new OrderException("product is not in stock");
        }

        var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        var orderLineItemList = orderLineItemDtoList.stream()
                .map(OrderLineItemMapper.INSTANCE::doMap)
                .toList();
        order.setOrderLineItemList(orderLineItemList);

        orderRepository.save(order);
    }
}
