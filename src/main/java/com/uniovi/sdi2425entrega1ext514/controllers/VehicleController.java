package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.LogService;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.RefuelService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.validators.VehicleRegistrationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleRegistrationValidation vehicleRegistrationValidation;
    private final PathService pathService;
    private final RefuelService refuelService;

    @Autowired
    private LogService logService;

    public VehicleController(VehicleService vehicleService,
                             VehicleRegistrationValidation vehicleRegistrationValidation,
                             PathService pathService, RefuelService refuelService) {
        this.vehicleService = vehicleService;
        this.pathService = pathService;
        this.refuelService = refuelService;
        this.vehicleRegistrationValidation = vehicleRegistrationValidation;
    }

    @RequestMapping("/vehicle/list")
    public String list(Model model, Pageable pageable) {

        Pageable pageableWithSize = PageRequest.of(pageable.getPageNumber(), 5);
        Page<Vehicle> vehicles = vehicleService.findAll(pageableWithSize);

        model.addAttribute("vehicles", vehicles.getContent());
        model.addAttribute("page", vehicles);
        return "vehicle/listVehicles";
    }

    @RequestMapping("/vehicle/list/update")
    public String update(Model model, Pageable pageable) {
        Page<Vehicle> vehicles = vehicleService.findAll(pageable);
        model.addAttribute("vehicles", vehicles.getContent());
        return "vehicle/list :: vehiclesTable";
    }

    @RequestMapping(value = "/vehicle/delete", method = RequestMethod.POST)
    public String deleteVehicles(@RequestParam("selectedVehicles") List<String> plates) {
        vehicleService.deleteVehicles(plates);
        return "redirect:/vehicle/list";
    }


    @RequestMapping(value = "/vehicle/register", method = RequestMethod.POST)
    public String register(@Validated Vehicle vehicle, BindingResult result, Model model) {
        vehicleRegistrationValidation.validate(vehicle, result);

        if (result.hasErrors()) {

            model.addAttribute("fuelTypes", Vehicle.FUEL_TYPES.values());
            return "vehicle/registerVehicle";
        }

        vehicleService.addVehicle(vehicle);

        //obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //guardar el log
        logService.saveLog(username, "REGISTER_VEHICLE", "/vehicle/register");

        return "redirect:/vehicle/list";
    }

    @RequestMapping(value = "/vehicle/register", method = RequestMethod.GET)
    public String register(Model model) {

        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("fuelTypes", Vehicle.FUEL_TYPES.values());
        return "vehicle/registerVehicle";
    }





}
