package com.bytebites.restaurant_service.repository;

import com.bytebites.restaurant_service.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);

    boolean existsByIdAndRestaurantId(Long menuItemId, Long restaurantId);
}