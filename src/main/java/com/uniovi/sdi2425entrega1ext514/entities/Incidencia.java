package com.uniovi.sdi2425entrega1ext514.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Incidencia {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_plate")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;
    private String title;

    private boolean requiresResponse;

    //solo si requiere respuesta
    private String response;
    private String state;

    private LocalDateTime date;


    public Incidencia() {}

    public Incidencia(Long id, Vehicle vehicle, User user, String description, boolean requiresResponse,
                      String response, String state) {

        this.id = id;
        this.vehicle = vehicle;
        this.user = user;
        this.description = description;
        this.requiresResponse = requiresResponse;
        this.response = response;
        this.state = state;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public LocalDateTime getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setRequiresResponse(boolean requiresResponse) {
        this.requiresResponse = requiresResponse;
    }
    public boolean getRequiresResponse() {
        return requiresResponse;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public String getResponse() {
        return response;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }


    public String toString() {
        return "Title: " + getTitle() + ", Description: " + getDescription() + ", State: " + getState() + ", Vehicle: "
                + getVehicle().getPlate() + ", User: " + getUser().getDni() + ", Response: " + getResponse();
    }

}