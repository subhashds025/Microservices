package com.propertyservice.service;

import com.propertyservice.controller.PropertyController;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomsDto;
import com.propertyservice.entity.*;
import com.propertyservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GcpStorageService gcpStorageService;

    @Autowired
    private PropertyPhotosRepository propertyPhotosRepository;



    public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {
        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());

        Property property = new Property();
        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
        property.setArea(area);
        property.setCity(city);
        property.setState(state);

        Property savedProperty = propertyRepository.save(property);

        // Save rooms
        for (RoomsDto roomsDto : dto.getRooms()) {
            Rooms rooms = new Rooms();
            rooms.setProperty(savedProperty);
            rooms.setRoomType(roomsDto.getRoomType());
            rooms.setBasePrice(roomsDto.getBasePrice());
            roomRepository.save(rooms);
        }

        List<String> urls = gcpStorageService.uploadImages(files);
        PropertyPhotos propertyPhotos = new PropertyPhotos();
        for(String s:urls){
            propertyPhotos.setUrl(s);
            propertyPhotos.setProperty(savedProperty);
            propertyPhotosRepository.save(propertyPhotos);
        }
        return dto;
    }


    public List<Property> getProperty(String search, LocalDate date) {
        List<Property> prop = propertyRepository.searchProperty(search,date);
    return prop;
    }

    public List<Rooms> getRooms(String searchName,LocalDate searchDate){
        List<Rooms> room = propertyRepository.findByStateCityAreaAndDate(searchName,searchDate);
        return room;
    }
}
