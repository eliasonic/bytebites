package com.bytebites.auth_service.mapper;

import com.bytebites.auth_service.dto.UserResponse;
import com.bytebites.auth_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toDto(User user);
}