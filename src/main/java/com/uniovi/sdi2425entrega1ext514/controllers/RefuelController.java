package com.uniovi.sdi2425entrega1ext514.controllers;


import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.LogService;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.RefuelService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.validators.RefuelValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/refuel")
public class RefuelController {

    private final RefuelService refuelService;
    private final PathService pathService;
    private final RefuelValidator refuelValidator;
    private final VehicleService vehicleService;
    private final LogService logService;

    public RefuelController(RefuelService refuelService, PathService pathService, RefuelValidator refuelValidator, VehicleService vehicleService, LogService logService) {
        this.refuelService = refuelService;
        this.pathService = pathService;
        this.refuelValidator = refuelValidator;
        this.vehicleService = vehicleService;
        this.logService = logService;
    }

    /**
     * Metodo GET que renderiza un html para listar los repostajes
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/list")
    public String showRefuelsFromVehicle(Model model, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/refuel/list");

        List<Vehicle> allVehicles = vehicleService.findAll();

        model.addAttribute("vehicles", allVehicles);

        return "refuel/list";
    }

    /**
     * Metodo GET que renderiza un html con los repostajes de cierto vehiculo
     * @param model
     * @param principal
     * @param plate
     * @param pageable
     * @return
     */
    @GetMapping("/vehicle/{plate}")
    public String showRefuelsFromCar(Model model, Principal principal,  @PathVariable String plate, Pageable pageable) {

        logService.saveLog(getCurrentUsername(), "PET", "/refuel/vehicle/" + plate);

        Page<Refuel> allRefuelsFromVehicle = refuelService.getRefuelsByVehicle(plate, pageable);
        model.addAttribute("refuelsList", allRefuelsFromVehicle.getContent());
        model.addAttribute("page", allRefuelsFromVehicle);

        Vehicle v = vehicleService.findByPlate(plate);
        model.addAttribute("vehicle", v);


        return"refuel/listFromCar";
    }


    /**
     * Metodo GET que renderiza el html que nos permite registrar un nuevo repostaje
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/new")
    public String showRefuelForm(Model model, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/refuel/new");

        String userDni = principal.getName();
        Path activePath = pathService.getActivePathForUser(userDni);

        if(activePath == null) {
            model.addAttribute("error", "No tienes un trayecto en curso para registrar un repostaje.");
            return "redirect:/home";
        }

        Refuel refuel = new Refuel();
        refuel.setOdometer(activePath.getInitialOdometer());

        model.addAttribute("activePath", activePath);
        model.addAttribute("refuel", refuel);
        return "refuel/new";
    }


    /**
     * Metodo POST que nos permite registrar un nuevo repostaje
     * @param refuel
     * @param result
     * @param principal
     * @param model
     * @return
     */
    @PostMapping("/new")
    public String registerRefuel(@ModelAttribute("refuel") Refuel refuel, BindingResult result,
                                 Principal principal, Model model) {

        logService.saveLog(getCurrentUsername(), "ALTA", "/refuel/new");

        String userDni = principal.getName();
        Path activePath = pathService.getActivePathForUser(userDni);

        if(activePath == null) {
            result.reject("error.refuel", "No tienes un trayecto en curso para registrar un repostaje.");
            return "refuel/new";
        }

        Vehicle vehicle = vehicleService.findByPlate(activePath.getVehicleRegistration()); // Buscar vehículo por matrícula
        if (vehicle == null) {
            result.reject("error.refuel", "Debes asociar un vehículo al repostaje.");
            return "refuel/new";
        }
        refuel.setVehicleRegistration(activePath.getVehicleRegistration());
        refuel.setVehicle(vehicle); // Asociar el vehículo encontrado al repostaje

        refuelValidator.validate(refuel, result);

        if(result.hasErrors()){
            model.addAttribute("activePath", activePath);
            return "refuel/new";
        }

        activePath.setKilometers(refuel.getOdometer());

        refuel.setDateTime(new Date());
        refuelService.saveRefuel(refuel);
        return "redirect:/vehicle/free";
    }

    /**
     * Metodo para la identificacion del usuario que está en sesion, utilizado para los logs
     * @return
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
