package com.propertyservice.repository;

import com.propertyservice.entity.PropertyPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyPhotosRepository extends JpaRepository<PropertyPhotos,Long> {
}
