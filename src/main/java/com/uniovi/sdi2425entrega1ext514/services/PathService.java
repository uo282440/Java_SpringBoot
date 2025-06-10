package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.repositories.PathRepository;
import com.uniovi.sdi2425entrega1ext514.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PathService {

    private final PathRepository pathRepository;
    private final HttpSession httpSession;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public PathService(PathRepository pathRepository, HttpSession httpSession,
                       VehicleRepository vehicleRepository) {
        this.pathRepository = pathRepository;
        this.httpSession = httpSession;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Verifica si existe un trayecto activo para el vehículo dado.
     * El trayecto está activo si finalOdometer es 0.
     *
     * @param vehicleRegistration Matrícula del vehículo.
     * @return true si existe un trayecto activo; false si no.
     */
    public boolean hasActivePathForVehicle(String vehicleRegistration) {
        Optional<Path> activePath = pathRepository.findByVehicleRegistrationAndFinalOdometer(vehicleRegistration, 0);
        return activePath.isPresent();
    }


    /**
     * Obtiene el trayecto activo del usuario.
     *
     * @param userDni DNI del usuario.
     * @return Trayecto activo o null si no existe.
     */
    public Path getActivePathForUser(String userDni) {

        return pathRepository.findByUserDniAndFinalOdometer(userDni, 0.0).orElse(null);
    }

    /**
     * Devuelve todos los Trayectos en formato de paginacion
     * @param pageable
     * @return
     */
    public Page<Path> getPaths(Pageable pageable) {
        Page<Path> paths = pathRepository.findAll(pageable);
        return paths;
    }


    /**
     * Devuelve un trayecto segun su ID
     * @param id
     * @return
     */
    public Path getPath(Long id) {
        Path path = pathRepository.findById(id).isPresent() ? pathRepository.findById(id).get() : new Path();
        return path;
    }


    /**
     * Obtiene el ultimo valor del odometro de un vehiculo segun la matricula dada
     * Si no existe trayecto finalizado, retorna 0.
     *
     * @param vehicleRegistration Matrícula del vehículo.
     * @return Valor del odómetro final o 0.
     */
    public double getLastOdometerForVehicle(String vehicleRegistration) {
        return pathRepository.findTopByVehicleRegistrationAndFinalOdometerGreaterThanOrderByStartDateDesc(vehicleRegistration, 0)
                .map(Path::getFinalOdometer)
                .orElse(0.0);
    }

    public void setPathResend(boolean revised, Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String dni = auth.getName();
        Path path = pathRepository.findById(id).get();
        if (path.getUser().getDni().equals(dni)) {
            pathRepository.updateResend(revised, id);
        }
    }

    /**
     * Crea un trayecto activo para un vehiculo y Uusario
     * @param vehicleRegistration
     * @param userDni
     * @return
     */
    public Path createActivePathForVehicle(String vehicleRegistration, String userDni) {
        Path path = new Path();

        path.setVehicleRegistration(vehicleRegistration);
        path.setUserDni(userDni);
        path.setStartDate(LocalDateTime.now());

        double initialOdometer = getLastOdometerForVehicle(vehicleRegistration);
        path.setInitialOdometer(initialOdometer);
        path.setFinalOdometer(0.0);

        pathRepository.save(path);

        return path;
    }

    /**
     * Finaliza un trayecto activo.
     * Se actualiza el campo finalOdometer, se calcula el tiempo transcurrido y la distancia recorrida.
     *
     * @param path
     */
    public void endPath(Path path) {
        // Recuperar desde la BD el path real
        Path dbPath = pathRepository.findById(path.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trayecto no encontrado."));

        // Actualizar campos, por ejemplo:
        dbPath.setFinalOdometer(path.getFinalOdometer());
        dbPath.setObservations(path.getObservations());
        dbPath.setEndDate(LocalDateTime.now());

        // Guardar
        pathRepository.save(dbPath);
    }

    /**
     * Guarda Trayecto en el repositorio
     * @param path
     */
    public void addPath(Path path) {
        pathRepository.save(path);
    }


    /**
     * Inicia un nuevo trayecto para un vehículo.
     * fecha actual como fecha de inicio
     * se calcula el odómetro inicial a partir del último trayecto finalizado (si existe) y se marca el trayecto como activo
     * (finalOdometer = 0).
     *
     * @param path Trayecto a iniciar (debe tener asignada la matrícula del vehículo).
     */
    public void startPath(Path path) {
        path.setStartDate(LocalDateTime.now());
        double initialOdometer = getLastOdometerForVehicle(path.getVehicleRegistration());
        path.setInitialOdometer(initialOdometer);

        //finalOdometer a 0 para indicar que el trayecto está activo.
        path.setFinalOdometer(0.0);
        pathRepository.save(path);
    }

    /**
     * Borra un trayecto
     * @param id
     */
    public void deletePath(Long id) {

        pathRepository.deleteById(id);
    }

    /**
     * Devuelve un trayecto para el usuario
     * @param pageable
     * @param user
     * @return
     */
    public Page<Path> getPathsForUser(Pageable pageable, User user) {
        Page<Path> paths = new PageImpl<Path>(new LinkedList<Path>());
        paths = pathRepository.findAllByUser(pageable, user);
        return paths;
    }

    /**
     * Devuelve trayectos para un usuario segun su DNI
     * @param dni
     * @return
     */
    public List<Path> getPathsByUserDni(String dni) {

        return pathRepository.getPathsByUserDni(dni);
    }

    /**
     * Encontrar trayectos segun la matricula del vehiculo
     * @param plate
     * @param pageable
     * @return
     */
    public Page<Path> findByVehiclePlate(String plate, Pageable pageable) {
        Page<Path> paths = pathRepository.findAllByVehiclePlate(pageable, plate);
        return paths;
    }

    /**
     * Encontrar trayectos segun la matricula del vehiculo
     * @param plate
     * @return
     */
    public List<Path> getPathsByVehicle(String plate) {
        List<Path> result = pathRepository.getPathsByVehicle(plate);
        return result;
    }

    /**
     * Guarda los cambios en un trayecto
     * @param path
     */
    public void editPath(Path path) {
        pathRepository.save(path);
    }

}
