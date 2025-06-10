package com.uniovi.sdi2425entrega1ext514.repositories;


import com.uniovi.sdi2425entrega1ext514.entities.Incidencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IncidenciaRepository extends CrudRepository<Incidencia, Long> {

    Page<Incidencia> findAll(Pageable pageable);

    List<Incidencia> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE Incidencia i SET i.state = ?2 WHERE i.id = ?1")
    void updateOne(Long id, String state);

    @Query("SELECT i FROM Incidencia i WHERE i.id = ?1")
    Incidencia findOne(Long id);

    @Query("SELECT i FROM Incidencia i WHERE i.vehicle.plate = ?1 ORDER BY i.id ASC")
    List<Incidencia> getIncidenciasByVehicle(String plate);

    @Query("SELECT i FROM Incidencia i WHERE i.vehicle.plate = ?1 ORDER BY i.id ASC")
    Page<Incidencia> findAllByVehiclePlate(Pageable pageable, String plate);


    @Query("SELECT i FROM Incidencia i WHERE i.user.dni = ?1 ORDER BY i.id ASC")
    List<Incidencia> findAllByUserDNI(String dni);

    @Query("SELECT i FROM Incidencia i WHERE i.user.dni = ?1 ORDER BY i.id ASC")
    Page<Incidencia> findAllByUserDNI(String dni, Pageable pageable);


    @Query("SELECT i FROM Incidencia i WHERE i.user.dni = ?1 AND i.state = ?2 ORDER BY i.id ASC")
    List<Incidencia> findAllRegisteredByUserDNI(String dni, String state);

}