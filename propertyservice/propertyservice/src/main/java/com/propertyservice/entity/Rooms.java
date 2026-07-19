package com.propertyservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="rooms")
public class Rooms {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String roomType;

    @Column(name = "base_price")
    private double basePrice;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonBackReference
    private Property property;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomAvailability> roomAvailability = new ArrayList<>();

    public List<RoomAvailability> getRoomAvailability() {
        return roomAvailability;
    }

    public void setRoomAvailability(List<RoomAvailability> roomAvailability) {
        this.roomAvailability = roomAvailability;
    }

    public long getId() {
        return id;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public Property getProperty() {
        return property;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setProperty(Property property) {
        this.property = property;
    }



}
