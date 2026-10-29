package com.example.homestay.repository;

import com.example.homestay.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"order", "order.room", "order.room.homestay"})
    List<Review> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"order", "order.room", "order.room.homestay"})
    Optional<Review> findWithOrderById(Long id);

    boolean existsByOrderId(Long orderId);

    @Query("select coalesce(avg(r.rating), 0) from Review r where r.visible = true")
    double averageVisibleRating();
}
