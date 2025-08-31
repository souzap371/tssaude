package com.ts.saude.model;

public class CalendarDay {
    private String date; // Ex: 2025-08-14
    private String day; // Ex: "14"
    private boolean selected;

    public CalendarDay(String date, String day, boolean selected) {
        this.date = date;
        this.day = day;
        this.selected = selected;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public boolean isSelected() {
        return selected;
    }
}
