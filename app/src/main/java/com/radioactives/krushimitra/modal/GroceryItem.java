package com.radioactives.krushimitra.modal;

public class GroceryItem {
    private String name;
    private String location;
    private String farmName;
    private String costPerKg;
    private double latitude;
    private double longitude;
    private String farmerName;
    private String contact;

    public GroceryItem(String name, String location, String farmName, String costPerKg,
                       double latitude, double longitude, String farmerName, String contact) {
        this.name = name;
        this.location = location;
        this.farmName = farmName;
        this.costPerKg = costPerKg;
        this.latitude = latitude;
        this.longitude = longitude;
        this.farmerName = farmerName;
        this.contact = contact;
    }

    // Getters
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getFarmName() { return farmName; }
    public String getCostPerKg() { return costPerKg; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getFarmerName() { return farmerName; }
    public String getContact() { return contact; }
}
