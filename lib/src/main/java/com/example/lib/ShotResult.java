package com.example.lib;

public enum ShotResult {
    MISS("miss"),
    HIT("hit"),
    SUNK("dead");

    private final String message;

    ShotResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
