package hu.pantasandor.orderservice.controller;

import hu.pantasandor.orderservice.dto.OrderRequest;
import hu.pantasandor.orderservice.exception.OrderException;
import hu.pantasandor.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hu.pantasandor.orderservice.controller.OrderController.API_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(API_URL)
@RequiredArgsConstructor
public class OrderController {

    public static final String API_URL = "/api/v1/order";

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<String> placeOrder(@RequestBody @Valid OrderRequest orderRequest) throws OrderException {
        orderService.placeOrder(orderRequest);
        return ResponseEntity.status(CREATED).body("Order Placed Successfully");
    }

}
