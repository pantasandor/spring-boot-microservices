package hu.pantasandor.orderservice.mapper;

import hu.pantasandor.orderservice.dto.OrderRequest;
import hu.pantasandor.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order doMap(OrderRequest orderRequest);
}
