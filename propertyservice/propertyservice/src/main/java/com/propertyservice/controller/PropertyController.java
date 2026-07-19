package com.propertyservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.propertyservice.dto.RoomAvailabilityDto;
import com.propertyservice.entity.Property;
import com.propertyservice.entity.RoomAvailability;
import com.propertyservice.entity.Rooms;
import com.propertyservice.repository.RoomAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.service.PropertyService;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {


    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
    @PostMapping(
            value = "/add-property",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,  // Ensures the endpoint accepts multipart/form-data
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse> addProperty(
            @RequestParam("property") String propertyJson,  // Use RequestParam to get the property as a raw JSON string
            @RequestParam("files") MultipartFile[] files) {  // Use RequestParam to handle files

        // Log the multipart parts
//        logger.info("Property JSON: " + propertyJson);
//        logger.info("Number of files uploaded: " + (files != null ? files.length : 0));

        // Parse the property JSON into PropertyDto
        ObjectMapper objectMapper = new ObjectMapper();
        PropertyDto dto = null;
        try {
            dto = objectMapper.readValue(propertyJson, PropertyDto.class);  // Convert JSON string to PropertyDto
        } catch (Exception e) {
       //     logger.error("Error parsing property JSON", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Handle bad JSON
        }

        // Process the property and files
        PropertyDto property = propertyService.addProperty(dto, files);

        // Create response object
        APIResponse<PropertyDto> response = new APIResponse<>();
        response.setMessage("Property added");
        response.setStatus(201);
        response.setData(property);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/property")
    public ResponseEntity<APIResponse> getProperty(@RequestParam("searchName") String search,  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<Property> prop = propertyService.getProperty(search,date);
        APIResponse<List<Property>> response = new APIResponse<>();
        response.setMessage("successfull");
        response.setStatus(201);
        response.setData(prop);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/rooms")
    public ResponseEntity<APIResponse> getRooms(@RequestParam("searchName") String search,  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<Rooms> room = propertyService.getRooms(search,date);
        APIResponse<List<Rooms>> response = new APIResponse<>();
        response.setMessage("successfull");
        response.setStatus(201);
        response.setData(room);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @GetMapping("/property-id")
    public APIResponse<PropertyDto> getPropertyById(@RequestParam long id){
        APIResponse<PropertyDto> response = propertyService.findPropertyById(id);
        return response;
    }

    @GetMapping("/room-available-room-id")
    public APIResponse<List<RoomAvailabilityDto>> getTotalRoomsAvailable(@RequestParam long id){
        List<RoomAvailabilityDto> totalRooms = propertyService.getTotalRoomsAvailable(id);



        APIResponse<List<RoomAvailabilityDto>> response = new APIResponse<>();
        response.setMessage("Total rooms");
        response.setStatus(200);
        response.setData(totalRooms);
        return response;
    }

    @GetMapping("/room-id")
    public APIResponse<Rooms> getRoomType(@RequestParam long id){
        Rooms room = propertyService.getRoomById(id);

        APIResponse<Rooms> response = new APIResponse<>();
        response.setMessage("Total rooms");
        response.setStatus(200);
        response.setData(room);
        return response;
    }

    @PutMapping("/roomcount")
    public String reduceRoomCount(@RequestBody Map<Long, LocalDate> roomData) {
        for (Map.Entry<Long, LocalDate> entry : roomData.entrySet()) {

            RoomAvailability roomAvailability  = roomAvailabilityRepository.findByRoomIdAndAvailableDate(entry.getKey(),entry.getValue());
           int count= roomAvailability.getAvailableCount();
           roomAvailability.setAvailableCount(count-1);
            roomAvailabilityRepository.saveAndFlush(roomAvailability);
        }
        return "Count Reduced";
    }
}
