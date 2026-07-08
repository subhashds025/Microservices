package com.propertyservice.controller;

import java.time.LocalDate;
import java.util.List;

import com.propertyservice.entity.Property;
import com.propertyservice.entity.Rooms;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.service.PropertyService;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

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
}
