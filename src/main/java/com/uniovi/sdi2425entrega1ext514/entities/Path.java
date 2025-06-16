package com.uniovi.sdi2425entrega1ext514.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
public class Path {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime startDate;
    private double time;                // Tiempo del recorrido
    private double initialOdometer;     // Kilometros antes del reccorido del recorrido
    private Double finalOdometer;       // Kilometros despues del recorrido
    private String vehicleRegistration; // Matricula del coche

    private LocalDateTime endDate; //LocalDateTime  Date
    private String observations;

    private static final DateTimeFormatter HTML_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private double kilometers;          // Kilometros del trayecto

    @Column(name = "user_dni")
    private String userDni;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Path(){}

    public Path(Long id, LocalDateTime startDate, double time, double initialOdometer, double finalOdometer,
                String vehicleRegistration, double kilometers) {

        this.id = id;
        this.startDate = startDate;
        this.time = time;
        this.initialOdometer = initialOdometer;
        this.finalOdometer = finalOdometer;
        this.vehicleRegistration = vehicleRegistration;
        this.kilometers = kilometers;
    }
    public String getUserDni() {
        return userDni;
    }

    public Path(LocalDateTime startDate, double time, double initialOdometer, double finalOdometer,
                String vehicleRegistration, double kilometers, User user) {
        super();
        this.startDate = startDate;
        this.time = time;
        this.initialOdometer = initialOdometer;
        this.finalOdometer = finalOdometer;
        this.vehicleRegistration = vehicleRegistration;
        this.kilometers = kilometers;
        this.user = user;
        this.userDni = user.getDni();
    }

    public void setUserDni(String userDni) {
        this.userDni = userDni;
    }
    public Long getId() {
        return id;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }


    public String getOnlyDate() {
        return startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getOnlyTime() {
        return startDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getHtmlFormattedStartDate() {
        return startDate != null ? startDate.format(HTML_DATETIME_FORMATTER) : "";
    }

    public String getHtmlFormattedEndDate() {
        return endDate != null ? endDate.format(HTML_DATETIME_FORMATTER) : "";
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getInitialOdometer() {
        return initialOdometer;
    }

    public void setInitialOdometer(double initialOdometer) {
        this.initialOdometer = initialOdometer;
    }

    public Double getFinalOdometer() {
        return finalOdometer;
    }

    public void setFinalOdometer(Double finalOdometer) {
        this.finalOdometer = finalOdometer;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(id, path.id);
    }

    @Override
    public String toString() {
        return "Path {" + "id=" + id + " startDate=" + startDate +
                " time=" + time + " initialOdometer=" + initialOdometer +
                " finalOdometer=" + finalOdometer +
                " vehicleRegistration=" + vehicleRegistration +
                " kilometers=" + kilometers + "}";
    }
}
