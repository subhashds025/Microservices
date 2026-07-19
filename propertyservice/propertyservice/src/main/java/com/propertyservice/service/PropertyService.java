package com.propertyservice.service;

import com.propertyservice.controller.PropertyController;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomAvailabilityDto;
import com.propertyservice.dto.RoomsDto;
import com.propertyservice.entity.*;
import com.propertyservice.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    private final RoomAvailabilityRepository roomAvailabilityRepository;

    private PropertyService(RoomAvailabilityRepository roomAvailabilityRepository){
        this.roomAvailabilityRepository = roomAvailabilityRepository;
    }



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


    public APIResponse<PropertyDto> findPropertyById(long id){
        APIResponse<PropertyDto> response = new APIResponse<>();
        PropertyDto dto  = new PropertyDto();
        Optional<Property> opProp = propertyRepository.findById(id);
        if(opProp.isPresent()) {
            Property property = opProp.get();
            dto.setArea(property.getArea().getName());
            dto.setCity(property.getCity().getName());
            dto.setState(property.getState().getName());
            List<Rooms> rooms = property.getRooms();
            List<RoomsDto> roomsDto = new ArrayList<>();
            for(Rooms room:rooms) {
                RoomsDto roomDto = new RoomsDto();
                BeanUtils.copyProperties(room, roomDto);
                roomsDto.add(roomDto);
            }
            dto.setRooms(roomsDto);
            BeanUtils.copyProperties(property, dto);
            response.setMessage("Matching Record");
            response.setStatus(200);
            response.setData(dto);
            return response;
        }

        return null;
    }

    public List<RoomAvailabilityDto> getTotalRoomsAvailable(long id) {
        List<RoomAvailability> roomAvailabilityList=  roomAvailabilityRepository.findByRoomId(id);
        List<RoomAvailabilityDto> dtoList = new ArrayList<>();

        for (RoomAvailability ra : roomAvailabilityList) {

            RoomAvailabilityDto dto = new RoomAvailabilityDto();

            dto.setId(ra.getId());
            dto.setAvailableDate(ra.getAvailableDate());
            dto.setAvailableCount(ra.getAvailableCount());
            dto.setPrice(ra.getPrice());
            dto.setRoomId(ra.getRoom().getId());


            dtoList.add(dto);
        }

        return dtoList;


    }

    public Rooms getRoomById(long id) {
        return roomRepository.findById(id).get();
    }

}
