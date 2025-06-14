package com.uniovi.sdi2425entrega1ext514.controllers;


import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.RefuelService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.validators.RefuelValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public RefuelController(RefuelService refuelService, PathService pathService, RefuelValidator refuelValidator, VehicleService vehicleService) {
        this.refuelService = refuelService;
        this.pathService = pathService;
        this.refuelValidator = refuelValidator;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/list")
    public String showRefuelsFromVehicle(Model model, Principal principal) {

        List<Vehicle> allVehicles = vehicleService.findAll();

        model.addAttribute("vehicles", allVehicles);

        return "refuel/list";
    }

    @GetMapping("/vehicle/{plate}")
    public String showRefuelsFromCar(Model model, Principal principal,  @PathVariable String plate, Pageable pageable) {


        Page<Refuel> allRefuelsFromVehicle = refuelService.getRefuelsByVehicle(plate, pageable);
        model.addAttribute("refuelsList", allRefuelsFromVehicle.getContent());
        model.addAttribute("page", allRefuelsFromVehicle);

        Vehicle v = vehicleService.findByPlate(plate);
        model.addAttribute("vehicle", v);


        return"refuel/listFromCar";
    }


    @GetMapping("/new")
    public String showRefuelForm(Model model, Principal principal) {
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

    @PostMapping("/new")
    public String registerRefuel(@ModelAttribute("refuel") Refuel refuel, BindingResult result,
                                 Principal principal, Model model) {

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
        // Llamada al validador para validar el objeto refuel
        refuelValidator.validate(refuel, result);

        if(result.hasErrors()){
            model.addAttribute("activePath", activePath);
            return "refuel/new";
        }

        activePath.setKilometers(refuel.getOdometer());
        System.out.println(refuel.getStationName()+ " " + refuel.getVehicleRegistration() + " ");

        refuel.setDateTime(new Date());
        refuelService.saveRefuel(refuel);
        return "redirect:/vehicle/free";
    }




}
