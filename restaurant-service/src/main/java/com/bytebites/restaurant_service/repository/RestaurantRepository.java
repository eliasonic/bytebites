package com.bytebites.restaurant_service.repository;

import com.bytebites.restaurant_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByOwnerId(String ownerId);

    boolean existsByIdAndOwnerId(Long restaurantId, String ownerId);
}