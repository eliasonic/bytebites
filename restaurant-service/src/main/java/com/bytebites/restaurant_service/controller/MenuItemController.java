package com.bytebites.restaurant_service.controller;

import com.bytebites.restaurant_service.dto.MenuItemDto;
import com.bytebites.restaurant_service.service.MenuItemService;
import com.bytebites.restaurant_service.service.impl.MenuItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService menuItemService;

    @GetMapping
    public List<MenuItemDto> getMenuItems(@PathVariable Long restaurantId) {
        return menuItemService.getMenuItemsByRestaurant(restaurantId);
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER') and @menuItemServiceImpl.isRestaurantOwner(#restaurantId, authentication.principal)")
    public MenuItemDto createMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItemDto menuItemDto) {
        return menuItemService.createMenuItem(restaurantId, menuItemDto);
    }

    @PutMapping("/{menuItemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') and @menuItemServiceImpl.isRestaurantOwner(#restaurantId, authentication.principal)")
    public MenuItemDto updateMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId, @RequestBody MenuItemDto menuItemDto) {
        return menuItemService.updateMenuItem(menuItemId, menuItemDto);
    }

    @DeleteMapping("/{menuItemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') and @menuItemServiceImpl.isRestaurantOwner(#restaurantId, authentication.principal)")
    public void deleteMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);
    }
}