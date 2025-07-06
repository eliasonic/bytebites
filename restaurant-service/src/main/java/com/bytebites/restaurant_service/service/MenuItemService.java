package com.bytebites.restaurant_service.service;

import com.bytebites.restaurant_service.dto.MenuItemDto;

import java.util.List;

public interface MenuItemService {
    List<MenuItemDto> getMenuItemsByRestaurant(Long restaurantId);

    MenuItemDto createMenuItem(Long restaurantId, MenuItemDto menuItemDto);

    MenuItemDto updateMenuItem(Long menuItemId, MenuItemDto menuItemDto);

    void deleteMenuItem(Long menuItemId);

    boolean isRestaurantOwner(Long restaurantId, String ownerId);
}
