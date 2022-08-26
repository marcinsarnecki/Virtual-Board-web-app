package com.ctdp.springproject.model;

public enum Color {
    GREEN, BLUE, RED, ORANGE, YELLOW;
    public String getUrl() {
        return switch (this) {
            case RED -> "red.png";
            case BLUE -> "blue.png";
            case GREEN -> "green.png";
            case ORANGE -> "orange.png";
            case YELLOW -> "yellow.png";
        };
    }
}
