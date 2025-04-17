package com.example.demo.controller;

import com.example.demo.annotation.RateLimit;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @RateLimit(limit = 2, duration = 10, timeUnit = TimeUnit.SECONDS, keyPrefix = "getRooms")

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/process/{roomId}")
    public ResponseEntity<String> processRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.simulateRoomProcessing(roomId));
    }

}
