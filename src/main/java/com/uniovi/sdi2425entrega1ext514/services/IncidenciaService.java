package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.Incidencia;
import com.uniovi.sdi2425entrega1ext514.repositories.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    /**
     * Metodo que nos devuelve todas las incidencias con paginacion
     * @param pageable
     * @return todas las incidencias
     */
    public Page<Incidencia> findAll(Pageable pageable) {
        Page<Incidencia> incidencias = incidenciaRepository.findAll(pageable);
        return incidencias;
    }

    /**
     * Guarda una incidencia en el repositorio
     * @param incidencia
     */
    public void save(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
    }

    /**
     * Devuelve todas las incidencias segun la matricula del coche
     * @param plate
     * @param pageable
     * @return
     */
    public Page<Incidencia> findAllByVehiclePlate(String plate, Pageable pageable) {
        Page<Incidencia> incidencias = incidenciaRepository.findAllByVehiclePlate(pageable, plate);
        return incidencias;
    }

    /**
     * Devuelve todas las incidencias segun el DNI del usuario
     * @param dni
     * @param pageable
     * @return
     */
    public Page<Incidencia> findAllByUserDNI(String dni, Pageable pageable) {
        Page<Incidencia> incidencias = incidenciaRepository.findAllByUserDNI(dni, pageable);
        return incidencias;
    }

    /**
     * Devuelve todas las incidencias segun el estado y el dni
     * @param dni
     * @param state
     * @return
     */
    public List<Incidencia> findByStateAndDNI(String dni, String state) {
        return incidenciaRepository.findAllRegisteredByUserDNI(dni, state);
    }

    /**
     * Encuentra todas las incidencias
     * @return todas las incidencias
     */
    public List<Incidencia> findAllList() {
        return incidenciaRepository.findAll();
    }

    /**
     * Encuentra una Incidencia segun su ID
     * @param id
     * @return
     */
    public Incidencia findOne(Long id) {
        return incidenciaRepository.findOne(id);
    }

    /**
     * Actualiza una incidencia
     * @param id
     * @param state
     */
    public void updateOne(Long id, String state) {
        incidenciaRepository.updateOne(id, state);
    }

}
