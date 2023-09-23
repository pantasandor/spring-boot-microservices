package hu.pantasandor.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {

    private Long id;
    @NotEmpty
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;

}
