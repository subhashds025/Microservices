package com.propertyservice.repository;

import java.time.LocalDate;
import java.util.List;

import com.propertyservice.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.propertyservice.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT DISTINCT p from Property p JOIN p.city c JOIN p.state s JOIN p.area a JOIN p.rooms r JOIN r.roomAvailability ra" +
            " WHERE (LOWER (c.name) like LOWER(CONCAT('%', :search ,'%')) or LOWER(s.name) like LOWER(CONCAT('%', :search, '%')) or LOWER(a.name) like LOWER(CONCAT('%', :search ,'%'))) and ra.availableDate = :date")
    List<Property> searchProperty(@Param("search") String search, @Param("date") LocalDate date);


    @Query("SELECT r from Rooms r join r.roomAvailability ra where " +
            "(LOWER(r.property.state.name) like LOWER(CONCAT('%', :searchName ,'%')) " +
            "or LOWER(r.property.city.name) like LOWER(CONCAT('%', :searchName ,'%')) " +
            "or LOWER(r.property.area.name) like LOWER(CONCAT('%', :searchName , '%')))" +
            "and ra.availableDate = :searchDate ")
    List<Rooms> findByStateCityAreaAndDate(@Param("searchName")String searchName,@Param("searchDate")LocalDate searchDate);
}
