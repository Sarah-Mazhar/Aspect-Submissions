
package com.example.demo.service;

import com.example.demo.annotation.DistributedLock;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private static final String ALL_ROOMS_CACHE_KEY = "rooms:all";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RedisClient redisClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Room> getAllRooms() {
        try {
            String cachedJson = redisClient.get(ALL_ROOMS_CACHE_KEY);
            if (cachedJson != null) {
                logger.info(" Cache hit – returning rooms from Redis");
                return objectMapper.readValue(cachedJson, new TypeReference<List<Room>>() {});
            }
        } catch (Exception e) {
            logger.error(" Error reading from Redis cache", e);
        }

        logger.info(" Cache miss – querying MySQL database for rooms");
        List<Room> rooms = roomRepository.findAll();

        try {
            String json = objectMapper.writeValueAsString(rooms);
            redisClient.set(ALL_ROOMS_CACHE_KEY, json, CACHE_TTL);
            logger.info("Rooms cached in Redis with TTL: {}", CACHE_TTL);
        } catch (Exception e) {
            logger.error(" Error writing rooms to Redis cache", e);
        }

        return rooms;
    }

    @DistributedLock(keyPrefix = "lockRoom", keyIdentifierExpression = "#roomId", leaseTime = 10, timeUnit = TimeUnit.SECONDS)
    public String simulateRoomProcessing(Long roomId) {
        try {
            System.out.println(" Processing room ID: " + roomId + " by " + Thread.currentThread().getName());
            Thread.sleep(10000); // Simulate a 10-second operation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return " Done processing room: " + roomId;
    }
}
