package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.repositories.RefuelRepository;
import com.uniovi.sdi2425entrega1ext514.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Service
public class RefuelService {

    @Autowired
    private RefuelRepository refuelRepository;


    private final HttpSession httpSession;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    public RefuelService(HttpSession httpSession) {

        this.httpSession = httpSession;
    }


    public RefuelService(RefuelRepository refuelRepository, HttpSession httpSession) {
        this.refuelRepository = refuelRepository;
        this.httpSession = httpSession;
    }


    /**
     * Devuelve todos los repostajes
     * @param pageable
     * @return
     */
    public Page<Refuel> getRefuels(Pageable pageable) {
        Page<Refuel> refuels = refuelRepository.findAll(pageable);
        return refuels;
    }


    /**
     * Devuelve un repostaje segun el id
     * @param id
     * @return
     */
    public Refuel getRefuel(Long id) {
        Refuel refuel = refuelRepository.findById(id).isPresent() ? refuelRepository.findById(id).get() : new Refuel();
        return refuel;
    }

    /**
     * Añade refuel al repositorio
      * @param path
     */
    public void addRefuel(Refuel path) {
        refuelRepository.save(path);
    }

    /**
     * Guarda el repostaje, asignando la fecha y hora actual.
     */
    public void saveRefuel(Refuel refuel){
        refuel.setDateTime(new Date());
        refuelRepository.save(refuel);
    }

    /**
     * Encuentra vehiculos segun la matricula del vehiculo
     * @param plate
     * @param pageable
     * @return
     */
    public Page<Refuel> findByVehiclePlate(String plate, Pageable pageable) {
        Page<Refuel> refuels = refuelRepository.findAllByVehiclePlate(pageable, plate);
        return refuels;
    }


    /**
     * Devuelve los repostajes segun el vehiculo, con paginacion
     * @param plate
     * @return
     */
    public Page<Refuel> getRefuelsByVehicle(String plate, Pageable pageable) {
        Page<Refuel> result = refuelRepository.getRefuelsByVehicle(plate, pageable);
        return result;
    }


    /**
     * Devuelve los repostajes segun el vehiculo, sin paginacion
     * @param plate
     * @return
     */
    public List<Refuel> getRefulsByVehicle(String plate) {
        List<Refuel> result = refuelRepository.getRefulsByVehicle(plate);
        return result;
    }

}
