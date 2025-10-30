package com.example.climate;

public class WeatherModel {
    private int id;
    private String city;
    private double temperature;
    private int humidity;
    private String description;
    private long timezone;

    public WeatherModel(int id, String city, double temperature, int humidity, String description, long timezone) {
        this.id = id;
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.timezone = timezone;
    }

    public WeatherModel(String city, double temperature, int humidity, String description, long timezone) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.timezone = timezone;
    }

    // Getters
    public int getId() { return id; }
    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public String getDescription() { return description; }
    public long getTimezone() { return timezone; }
}
