package com.uniovi.sdi2425entrega1ext514.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="vehicle")
public class Vehicle {

    public enum FUEL_TYPES {
        GASOLINA, DIESEL, MICROHIBRIDO, HIBRIDO, ELECTRICO, GLP, GNL
    }

    @Id
    @GeneratedValue
    private Long id;
    private String plate;           // Matrícula
    @Column(unique = true)
    private String chassisNumber;   // Número de bastidor
    private String brandName;       // Marca
    private String model;           // Modelo
    private double odometer;

    @Enumerated(EnumType.STRING)
    private FUEL_TYPES fuelType;
    private boolean isFree;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private Set<Refuel> refuels;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private Set<Incidencia> incidencias;

    public Vehicle() {}

    public Vehicle(Long id, String plate, String chassisNumber, String brandName, String model, FUEL_TYPES fuelType, double odometer) {
        this.id = id;
        this.plate = plate;
        this.chassisNumber = chassisNumber;
        this.brandName = brandName;
        this.model = model;
        this.fuelType = fuelType;
        this.isFree = true;
        this.odometer = odometer;
    }

    public Vehicle(String plate, String chassisNumber, String brandName, String model, FUEL_TYPES fuelType, double odometer) {
        super();
        this.plate = plate;
        this.chassisNumber = chassisNumber;
        this.brandName = brandName;
        this.model = model;
        this.fuelType = fuelType;
        this.isFree = true;
        this.odometer = odometer;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public FUEL_TYPES getFuelType() {
        return fuelType;
    }

    public void setFuelType(FUEL_TYPES fuelType) {
        this.fuelType = fuelType;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }
}
