package com.example.aptease.entity;

public enum AptService { 
    GROCERY("Grocery Shopping"),
    FOOD("Food Delivery"),
    LAUNDRY("Laundry Service"),
    WATER("Water Supply"),
    PET_CARE("Pet Care"),
    MAINTENANCE("Maintenance and Repairs");

    private final String displayName;

    AptService(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


