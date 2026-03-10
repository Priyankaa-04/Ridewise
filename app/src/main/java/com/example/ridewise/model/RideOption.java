package com.example.ridewise.model;

public class RideOption {
    private String provider;
    private String type;   // Cab / Auto / Bike
    private String eta;
    private double price;

    public RideOption(String provider, String type, String eta, double price) {
        this.provider = provider;
        this.type = type;
        this.eta = eta;
        this.price = price;
    }

    public String getProvider() { return provider; }
    public String getType() { return type; }
    public String getEta() { return eta; }
    public double getPrice() { return price; }
}
