package hu.pantasandor.productservice.mapper;

import hu.pantasandor.productservice.dto.ProductResponse;
import hu.pantasandor.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductResponse doMap(Product product);
}
