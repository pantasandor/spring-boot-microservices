package hu.pantasandor.orderservice.mapper;

import hu.pantasandor.orderservice.dto.OrderLineItemDto;
import hu.pantasandor.orderservice.model.OrderLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderLineItemMapper {
    OrderLineItemMapper INSTANCE = Mappers.getMapper(OrderLineItemMapper.class);

    OrderLineItem doMap(OrderLineItemDto orderLineItemDto);
}
