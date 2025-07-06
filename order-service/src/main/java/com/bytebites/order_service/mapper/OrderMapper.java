package com.bytebites.order_service.mapper;

import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderDto dto);
    OrderDto toDto(Order entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromDto(OrderDto dto, @MappingTarget Order entity);
}