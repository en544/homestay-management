package com.example.homestay.domain;

public enum RoomStatus {
    AVAILABLE("可预订", "success"),
    CLEANING("保洁中", "warning"),
    MAINTENANCE("维护中", "danger");

    private final String label;
    private final String tone;

    RoomStatus(String label, String tone) {
        this.label = label;
        this.tone = tone;
    }

    public String getLabel() {
        return label;
    }

    public String getTone() {
        return tone;
    }
}
