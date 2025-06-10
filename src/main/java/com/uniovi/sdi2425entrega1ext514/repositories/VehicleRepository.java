package com.uniovi.sdi2425entrega1ext514.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;

import java.util.List;

@Repository
public interface VehicleRepository extends CrudRepository<Vehicle, Long> {

    Vehicle findByPlate(String plate);

    Vehicle findByChassisNumber(String chassisNumber);

    @Query("SELECT r FROM Path r WHERE r.vehicleRegistration = ?1 ORDER BY r.id ASC")
    Page<Vehicle> findAllByVehicle(Pageable pageable, Vehicle Vehicle);

    @Query("SELECT v FROM Vehicle v WHERE v.isFree = TRUE ORDER BY v.plate ASC")
    Page<Vehicle> findFree(Pageable pageable);

    Page<Vehicle> findAll(Pageable pageable);

    List<Vehicle> findAll();

    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.plate IN :plates")
    void deleteByPlates(@Param("plates") List<String> plates);
}
