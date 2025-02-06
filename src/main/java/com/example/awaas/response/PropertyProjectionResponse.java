package com.example.awaas.response;

public class PropertyProjectionResponse {
    private Long id;
    private String title;
    private String location;
    private Double price;
    private String type;

    public PropertyProjectionResponse() {
    }

    public PropertyProjectionResponse(Long id, String title, String location, Double price, String type) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.price = price;
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
