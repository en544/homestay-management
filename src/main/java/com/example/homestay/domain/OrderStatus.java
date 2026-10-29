package com.example.homestay.domain;

public enum OrderStatus {
    PENDING("待确认", "warning"),
    CONFIRMED("已确认", "info"),
    CHECKED_IN("已入住", "success"),
    COMPLETED("已完成", "neutral"),
    CANCELLED("已取消", "danger");

    private final String label;
    private final String tone;

    OrderStatus(String label, String tone) {
        this.label = label;
        this.tone = tone;
    }

    public String getLabel() {
        return label;
    }

    public String getTone() {
        return tone;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == CHECKED_IN;
    }
}
