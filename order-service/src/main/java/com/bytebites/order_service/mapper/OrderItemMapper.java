package com.bytebites.order_service.mapper;

import com.bytebites.order_service.dto.OrderItemDto;
import com.bytebites.order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto dto);
    OrderItemDto toDto(OrderItem entity);
}