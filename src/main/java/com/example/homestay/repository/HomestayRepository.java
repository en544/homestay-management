package com.example.homestay.repository;

import com.example.homestay.domain.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomestayRepository extends JpaRepository<Homestay, Long> {

    List<Homestay> findAllByActiveTrueOrderByCreatedAtDesc();

    List<Homestay> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrderByCreatedAtDesc(
            String name, String city);

    Optional<Homestay> findByIdAndActiveTrue(Long id);
}
