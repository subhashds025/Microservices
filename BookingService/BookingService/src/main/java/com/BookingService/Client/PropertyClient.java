package com.BookingService.Client;

import com.BookingService.Dto.APIResponse;
import com.BookingService.Dto.RoomAvailabilityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(name="PROPERTYSERVICE")
public interface PropertyClient {

//    @GetMapping("/api/v1/property/property-id")
//    public APIResponse<PropertyDto> getPropertyById(@RequestParam long id);
//
//    @GetMapping("/api/v1/property/room-id")
//    public APIResponse<Rooms> getRoomType(@RequestParam long id);


    @GetMapping("/api/v1/property/room-available-room-id")
    public APIResponse<List<RoomAvailabilityDto>> getTotalRoomsAvailable(@RequestParam long id);




    @PutMapping("/api/v1/property/roomcount")
    public String reduceRoomCount(@RequestBody Map<Long, LocalDate> roomData);

}
