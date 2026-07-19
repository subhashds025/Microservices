package com.BookingService.Controller;

import com.BookingService.Client.PropertyClient;
import com.BookingService.Dto.*;
import com.BookingService.Entity.BookingDate;
import com.BookingService.Entity.Bookings;
import com.BookingService.Repository.BookingDateRepository;
import com.BookingService.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    private final PropertyClient  propertyClient;


    public BookingController(RestTemplate restTemplate,
                             WebClient.Builder builder,PropertyClient  propertyClient) {
        this.restTemplate = restTemplate;
        this.webClient = builder.build();
        this.propertyClient = propertyClient;
    }


    @Autowired
    private BookingDateRepository bookingDateRepository;

    @Autowired
    private BookingRepository bookingRepository;


//    @Autowired
//    private WebClient webClient;

    @PostMapping("/add-to-cart")
    public  APIResponse<List<String>> cart(@RequestBody BookingDto bookingDto) {

        List<String> messages = new ArrayList<>();
        APIResponse<List<String>> apiResponse = new APIResponse<>();
        Map<Long,LocalDate> booked= new HashMap<>();


        ResponseEntity<APIResponse<PropertyDto>> response= restTemplate.exchange(
                "http://PROPERTYSERVICE/api/v1/property/property-id?id=" + bookingDto.getPropertyId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<APIResponse<PropertyDto>>() {}
        );

        APIResponse<PropertyDto> apiRes = response.getBody();




         APIResponse<Rooms> rooms  =       webClient
                .get()
                .uri("http://PROPERTYSERVICE/api/v1/property/room-id?id=" + bookingDto.getRoomId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<APIResponse<Rooms>>() {})
                .block();

        APIResponse<List<RoomAvailabilityDto>> totalRoomsAvailable =    propertyClient.getTotalRoomsAvailable(bookingDto.getRoomAvailabilityId());
        List<RoomAvailabilityDto> availableRooms = totalRoomsAvailable.getData();


        //Logic to check available rooms based on date and count
        for(LocalDate date: bookingDto.getDate()) {
//            boolean isAvailable = availableRooms.stream()
//                    .anyMatch(ra -> ra.getAvailableDate().equals(date) && ra.getAvailableCount()>0);

            Long roomId = availableRooms.stream()
                    .filter(ra -> ra.getAvailableDate().equals(date)
                            && ra.getAvailableCount() > 0)
                    .map(ra -> ra.getRoomId())
                    .findFirst()
                    .orElse(null);


         //   System.out.println("Date " + date + " available: " + isAvailable);

            if (roomId==null) {
                messages.add("Room not available on: " + date);
                apiResponse.setMessage("Sold Out");
                apiResponse.setStatus(500);
                apiResponse.setData(messages);
                return apiResponse;
            }else{
                booked.put(roomId,date);
            }

        }

        //Save it to Booking Table with status pending
        Bookings bookings = new Bookings();
        bookings.setName(bookingDto.getName());
        bookings.setEmail(bookingDto.getEmail());
        bookings.setMobile(bookingDto.getMobile());
        bookings.setPropertyName(apiRes.getData().getName());
        bookings.setStatus("pending");
        bookings.setTotalPrice(rooms.getData().getBasePrice()*bookingDto.getTotalNigths());
        Bookings savedBooking = bookingRepository.save(bookings);

        for(LocalDate date: bookingDto.getDate()) {
            BookingDate  bookingDate = new BookingDate();
            bookingDate.setDate(date);
            bookingDate.setBookings(savedBooking);
            bookingDateRepository.save(bookingDate);
        }

        if(savedBooking!=null){
            String message = propertyClient.reduceRoomCount(booked);
        }

        return null;
    }
}
