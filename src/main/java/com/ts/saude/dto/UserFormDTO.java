package com.ts.saude.dto;

import java.util.List;

public class UserFormDTO {

    private String username;
    private String password;
    private String fullName;
    private List<Long> rolesSelected;

    private List<String> availableDays;
    private List<String> availableHours;
    private Integer appointmentDuration;

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Long> getRolesSelected() {
        return rolesSelected;
    }

    public void setRolesSelected(List<Long> rolesSelected) {
        this.rolesSelected = rolesSelected;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

    public List<String> getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(List<String> availableHours) {
        this.availableHours = availableHours;
    }

    public Integer getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(Integer appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }
}
