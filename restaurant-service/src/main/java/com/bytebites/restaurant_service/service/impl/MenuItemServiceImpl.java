package com.bytebites.restaurant_service.service.impl;

import com.bytebites.restaurant_service.dto.MenuItemDto;
import com.bytebites.restaurant_service.entity.MenuItem;
import com.bytebites.restaurant_service.entity.Restaurant;
import com.bytebites.restaurant_service.exception.ResourceNotFoundException;
import com.bytebites.restaurant_service.mapper.MenuItemMapper;
import com.bytebites.restaurant_service.repository.MenuItemRepository;
import com.bytebites.restaurant_service.repository.RestaurantRepository;
import com.bytebites.restaurant_service.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;

    public List<MenuItemDto> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(menuItemMapper::toDto)
                .toList();
    }

    @Transactional
    public MenuItemDto createMenuItem(Long restaurantId, MenuItemDto menuItemDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        MenuItem menuItem = menuItemMapper.toEntity(menuItemDto);
        menuItem.setRestaurant(restaurant);
        return menuItemMapper.toDto(menuItemRepository.save(menuItem));
    }

    @Transactional
    public MenuItemDto updateMenuItem(Long menuItemId, MenuItemDto menuItemDto) {
        MenuItem existingItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        menuItemMapper.updateFromDto(menuItemDto, existingItem);
        return menuItemMapper.toDto(menuItemRepository.save(existingItem));
    }

    @Transactional
    public void deleteMenuItem(Long menuItemId) {
        menuItemRepository.deleteById(menuItemId);
    }

    public boolean isRestaurantOwner(Long restaurantId, String ownerId) {
        return restaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }
}