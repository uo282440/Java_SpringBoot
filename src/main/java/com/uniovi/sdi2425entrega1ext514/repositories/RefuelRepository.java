package com.uniovi.sdi2425entrega1ext514.repositories;

import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefuelRepository extends CrudRepository<Refuel, Long> {

    @Query("SELECT r FROM Refuel r WHERE r.vehicle.plate = ?1 ORDER BY r.id ASC")
    Page<Refuel> findAllByVehiclePlate(Pageable pageable, String plate);

    Page<Refuel> findAll(Pageable pageable);

    @Query("SELECT r FROM Refuel r WHERE r.vehicle.plate = ?1 ORDER BY r.id ASC")
    List<Refuel> getRefulsByVehicle(String plate);

    @Query("SELECT r FROM Refuel r WHERE r.vehicle.plate = ?1 ORDER BY r.id ASC")
    Page<Refuel> getRefuelsByVehicle(String plate, Pageable pageable);

}
