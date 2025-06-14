package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private PathService pathService;

    /**
     * Encuentra todos los vehiculos en formato paginable
     * @param pageable
     * @return
     */
    public Page<Vehicle> findAll(Pageable pageable) {
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return vehicles;
    }

    /**
     * añade un vehiculo al repositorio
     * @param vehicle
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    /**
     * Encunetra un vehiculo segun su matricula
     * @param plate
     * @return
     */
    public Vehicle findByPlate(String plate) {
        return vehicleRepository.findByPlate(plate);
    }


    /**
     * Encuentra un Vehiculo segun su Número de bastidor
     * @param chassisNumber
     * @return
     */
    public Vehicle findByChassisNumber(String chassisNumber) {
        return vehicleRepository.findByChassisNumber(chassisNumber);
    }

    /**
     * Borra una serie de vehiculos dadas sus matriculas
     * @param plates
     */
    public void deleteVehicles(List<String> plates) {

        for (String plate : plates) {

            Vehicle vehicle = vehicleRepository.findByPlate(plate);

            if (vehicle != null) {
                vehicleRepository.delete(vehicle);
            }
        }
    }


    /**
     * Devuevle una lista con todos los vehiculos
     * @return
     */
    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicles= new ArrayList<Vehicle>();
        vehicleRepository.findAll().forEach(vehicles::add);
        return vehicles;
    }

    /**
     * Devuelve la lista de vehículos disponibles. (No tienen trayecto activo)
     * @return Lista de vehículos disponibles.
     */
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> allVehicles = getVehicles();
        return allVehicles.stream()
                .filter(vehicle -> !pathService.hasActivePathForVehicle(vehicle.getPlate()))
                .collect(Collectors.toList());
    }

    /**
     * Encuentra Vehiculos libres con paginacion
     * @param pageable
     * @return
     */
    public Page<Vehicle> findFree(Pageable pageable) {
        Page<Vehicle> vehicles = vehicleRepository.findFree(pageable);
        return vehicles;
    }

    /**
     * Encuentra Vehiculos libres
     * @param
     * @return
     */
    public List<Vehicle> findFreeList() {
        List<Vehicle> vehicles = vehicleRepository.findFreeList();
        return vehicles;
    }

    /**
     * Devuevle un vehiculo segun su ID
     * @param id
     * @return
     */
    public Vehicle getVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).isPresent() ? vehicleRepository.findById(id).get() : new Vehicle();
        return vehicle;
    }

    /**
     * Devuelve una lista con todos los vehiculos
     * @return
     */
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles;
    }

    @Transactional
    public void updateState(String plate, boolean isFree) {
        vehicleRepository.updateVehicleState(plate, isFree);
    }

}
