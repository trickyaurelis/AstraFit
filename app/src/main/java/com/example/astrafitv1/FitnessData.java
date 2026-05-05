package com.example.astrafitv1;

public class FitnessData {
    private int heartRate;
    private int maxHeartRate;
    private int calories;
    private int totalCalories;
    private int activeMinutes;
    private String totalActiveTime;
    private double distance;
    private double totalDistance;

    public FitnessData(int heartRate, int maxHeartRate, int calories, int totalCalories, 
                       int activeMinutes, String totalActiveTime, double distance, double totalDistance) {
        this.heartRate = heartRate;
        this.maxHeartRate = maxHeartRate;
        this.calories = calories;
        this.totalCalories = totalCalories;
        this.activeMinutes = activeMinutes;
        this.totalActiveTime = totalActiveTime;
        this.distance = distance;
        this.totalDistance = totalDistance;
    }

    // Getters
    public int getHeartRate() { return heartRate; }
    public int getMaxHeartRate() { return maxHeartRate; }
    public int getCalories() { return calories; }
    public int getTotalCalories() { return totalCalories; }
    public int getActiveMinutes() { return activeMinutes; }
    public String getTotalActiveTime() { return totalActiveTime; }
    public double getDistance() { return distance; }
    public double getTotalDistance() { return totalDistance; }
}