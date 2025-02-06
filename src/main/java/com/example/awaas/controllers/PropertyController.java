package com.example.awaas.controllers;

import com.example.awaas.entities.Property;
import com.example.awaas.enums.PropertyTypeEnum;
import com.example.awaas.requests.CreatePropertyRequest;
import com.example.awaas.response.PropertyResponse;
import com.example.awaas.services.PropertyService;
import com.example.awaas.utilities.JwtUtility;
import jakarta.el.PropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private JwtUtility jwtUtility;

    @PostMapping
    public ResponseEntity<?> createProperty(
            @RequestPart("property") CreatePropertyRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader("Authorization") String token) {
        try {

            String ownerEmail = jwtUtility.extractEmail(token);

            PropertyResponse response = propertyService.createProperty(request, image, ownerEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProperty(
            @PathVariable Long id,
            @RequestBody CreatePropertyRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            String ownerEmail = jwtUtility.extractEmail(token);

            PropertyResponse response = propertyService.editProperty(id, request, ownerEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProperties(
            @RequestParam(required = false) String location, // Optional location filter
            @RequestParam(required = false) Double minPrice, // Optional minimum price filter
            @RequestParam(required = false) Double maxPrice, // Optional maximum price filter
            @RequestParam(required = false) PropertyTypeEnum type, // Optional property type filter
            @RequestParam(defaultValue = "0") int page, // Default page number
            @RequestParam(defaultValue = "10") int size // Default page size
    ) {
        try {
            return ResponseEntity.ok(propertyService.getAllProperties(location, minPrice, maxPrice, type, page, size));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getPropertyDetails(@PathVariable Long propertyId) {
        try {
            PropertyResponse property = propertyService.getProperty(propertyId);
            return ResponseEntity.ok(property);
        } catch (PropertyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Auth annotation and context holder user details
    @PostMapping("/{propertyId}/interest")
    public ResponseEntity<?> expressInterest(
            @PathVariable Long propertyId,
            @RequestHeader("Authorization") String token) {
        try {
            String userEmail = jwtUtility.extractEmail(token);
            propertyService.sendInterestEmail(propertyId, userEmail);
            return ResponseEntity.ok("Interest email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
