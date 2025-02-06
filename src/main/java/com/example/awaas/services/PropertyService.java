package com.example.awaas.services;

import com.example.awaas.dtos.PropertyDTO;
import com.example.awaas.dtos.UserDTO;
import com.example.awaas.enums.PropertyTypeEnum;
import com.example.awaas.managers.PropertyManager;
import com.example.awaas.managers.UserManager;
import com.example.awaas.mappers.PropertyMapper;
import com.example.awaas.requests.CreatePropertyRequest;
import com.example.awaas.response.PropertyProjectionResponse;
import com.example.awaas.response.PropertyResponse;
import com.example.awaas.utilities.EmailUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PropertyService {
    @Autowired
    private PropertyManager propertyManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private EmailUtility emailUtility;

    // Path to save uploaded images locally (adjust as needed)
    private final String UPLOAD_DIR = "/Users/omrajsharma/Documents/coding-ninjas/cn-awaas-vishwa-spring-be/uploads/";

    public PropertyResponse createProperty(CreatePropertyRequest request, MultipartFile image, String ownerEmail)
            throws IOException {
        // Find owner by email
        UserDTO owner = userManager.getByEmail(ownerEmail);
        if (owner == null) {
            throw new IllegalArgumentException("Owner not found");
        }

        // Save image file locally
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            // Get the original file name and sanitize it (replace spaces and special chars)
            String originalFileName = image.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // Generate a unique file name based on the current timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String uniqueFileName = timestamp + "_" + sanitizedFileName;

            // Combine directory path and unique file name
            String filePath = UPLOAD_DIR + uniqueFileName;

            // Save the image to the server
            image.transferTo(new File(filePath));
            imagePath = filePath;
        }

        // Create and save property
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setTitle(request.getTitle());
        propertyDTO.setDescription(request.getDescription());
        propertyDTO.setPrice(request.getPrice());
        propertyDTO.setLocation(request.getLocation());
        propertyDTO.setType(request.getType());
        propertyDTO.setOwner(owner);
        propertyDTO.setImageUrl(imagePath);

        return PropertyMapper.INSTANCE.toPropertyResponse(propertyManager.save(propertyDTO));
    }

    public PropertyResponse editProperty(Long propertyId, CreatePropertyRequest request, String ownerEmail) {
        // Find the property by ID
        PropertyDTO property = propertyManager.findById(propertyId);

        if (property == null) {
            throw new IllegalArgumentException("Property not found");
        }

        // Check if the user is the owner
        if (!property.getOwner().getEmail().equals(ownerEmail)) {
            throw new SecurityException("You are not authorized to edit this property");
        }

        // Update the property details
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPrice(request.getPrice());
        property.setLocation(request.getLocation());
        property.setType(request.getType());

        // Save the updated property
        return PropertyMapper.INSTANCE.toPropertyResponse(propertyManager.save(property));
    }

    // Get all properties with pagination
    public Page<PropertyProjectionResponse> getAllProperties(String location, Double minPrice, Double maxPrice,
            PropertyTypeEnum type, int page, int size) {
        return propertyManager.getAllProperties(location, minPrice, maxPrice, type, page, size);
    }

    public PropertyResponse getProperty(Long propertyId) {
        PropertyDTO property = propertyManager.findById(propertyId);
        if (property == null) {
            throw new IllegalArgumentException("Property not found");
        }
        return PropertyMapper.INSTANCE.toPropertyResponse(property);
    }

    public void sendInterestEmail(Long propertyId, String userEmail) {
        PropertyDTO property = propertyManager.findById(propertyId);

        if (property == null) {
            throw new IllegalArgumentException("Property not found");
        }

        UserDTO userDTO = userManager.getByEmail(userEmail);

        if (userDTO == null) {
            throw new IllegalArgumentException("User not found");
        }

        UserDTO ownerDTO = userManager.getByEmail(property.getOwner().getEmail());

        if (ownerDTO == null) {
            throw new IllegalArgumentException("Owner not found");
        }

        if (ownerDTO.getEmail().equals(userDTO.getEmail())) {
            throw new SecurityException("Cannot send interest email");
        }

        emailUtility.sendEmail(ownerDTO.getEmail(),
                "Interest in Your Property: " + property.getTitle(),
                "Dear " + ownerDTO.getName() + ",\n\n" +
                        "You have received interest for your property \"" + property.getTitle() + "\".\n\n" +
                        "Details:\n" +
                        "Name: " + userDTO.getName() + "\n" +
                        "Email: " + userDTO.getEmail() + "\n" +
                        "Best regards,\nYour Property Listing Platform");
    }
}
