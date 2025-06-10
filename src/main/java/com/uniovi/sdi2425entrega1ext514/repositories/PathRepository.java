package com.uniovi.sdi2425entrega1ext514.repositories;

import com.uniovi.sdi2425entrega1ext514.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.uniovi.sdi2425entrega1ext514.entities.Path;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathRepository extends CrudRepository<Path, Long> {

    // Busca un trayecto activo para un vehículo (finalOdometer == 0)
    Optional<Path> findByVehicleRegistrationAndFinalOdometer(String vehicleRegistration, double finalOdometer);

    @Query("SELECT r FROM Path r WHERE r.vehicleRegistration = ?1 ORDER BY r.id ASC")
    Page<Path> findAllByVehiclePlate(Pageable pageable, String plate);

    @Query("SELECT r FROM Path r  WHERE r.user = ?1 ORDER BY r.startDate ASC")
    Page<Path> findAllByUser(Pageable pageable, User user);

    @Query("SELECT r FROM Path r  WHERE r.user.dni = ?1 ORDER BY r.startDate ASC")
    List<Path> getPathsByUserDni(String dni);

    @Query("SELECT r FROM Path r WHERE r.vehicleRegistration = ?1  ORDER BY r.startDate ASC")
    List<Path> getPathsByVehicle(String plate);

    Page<Path> findAll(Pageable pageable);

    // Busca el último trayecto finalizado (finalOdometer > 0) para un vehículo, ordenado por startDate descendente.
    Optional<Path> findTopByVehicleRegistrationAndFinalOdometerGreaterThanOrderByStartDateDesc(String vehicleRegistration, double finalOdometer);

    // Busca el trayecto activo para un usuario
    Optional<Path> findByUserDniAndFinalOdometer(String userDni, double finalOdometer);

    @Modifying
    @Transactional
    @Query("UPDATE Path SET resend = ?1 WHERE id = ?2")
    void updateResend(Boolean resend, Long id);



}
