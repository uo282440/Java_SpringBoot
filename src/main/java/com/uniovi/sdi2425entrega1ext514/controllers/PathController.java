package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.validators.EndTripValidator;
import com.uniovi.sdi2425entrega1ext514.validators.StartTripValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;
    private final UsersService usersService;
    private final HttpSession httpSession;
    private final VehicleService vehicleService;

    @Autowired
    private EndTripValidator endTripValidator;
    @Autowired
    private StartTripValidator startTripValidator;


    public PathController(PathService pathService, UsersService usersService, HttpSession httpSession, VehicleService vehicleService) {
        this.pathService = pathService;
        this.usersService = usersService;
        this.httpSession = httpSession;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/list")
    public String getList(Model model, Pageable pageable, Principal principal) {

        Pageable pageableWithSize = PageRequest.of(pageable.getPageNumber(), 5);

        String dni = principal.getName();
        User user = usersService.getUserByDni(dni);

        Page<Path> paths = pathService.getPathsForUser(pageable, user);

        model.addAttribute("pathList", paths.getContent());
        model.addAttribute("page", paths);

        return "path/list";
    }

    @GetMapping("/start")
    public String showStartForm(Model model) {
        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        model.addAttribute("availableVehicles", availableVehicles);

        LocalDateTime localDateTime = LocalDateTime.now();
        model.addAttribute("path", new Path());
        return "path/start";
    }

    @PostMapping("/start")
    public String startPath(@ModelAttribute("path") Path path, @RequestParam("vehicleRegistration") String vehiclePlate, BindingResult result, Principal principal,
                            Model model) {

        // Asigna el DNI del usuario obtenido del Principal
        path.setUserDni(principal.getName());

        User user = usersService.getUserByDni(principal.getName());
        path.setUser(user);
        path.setVehicleRegistration(vehiclePlate);

        // Valida el objeto 'path' usando el StartTripValidator
        startTripValidator.validate(path, result);
        if (result.hasErrors()) {
            model.addAttribute("availableVehicles", vehicleService.getAvailableVehicles());
            return "path/start";
        }
        pathService.startPath(path);
        System.out.println(path.toString());
        System.out.println("Guardando trayecto con usuario: " + path.getUser());
        return "redirect:/home";
    }


    @GetMapping("/end")
    public String showEndForm(Model model, Principal principal) {
        Path activePath = pathService.getActivePathForUser(principal.getName());
        if (activePath == null) {
            model.addAttribute("error", "No tienes un trayecto en curso para finalizar.");
            return "redirect:/home";
        }
        model.addAttribute("activePath", activePath);
        return "path/end";
    }

    @PostMapping("/end")
    public String endPath(@ModelAttribute("activePath") Path path,
                          BindingResult result, Principal principal, Model model) {

        Path activePath = pathService.getActivePathForUser(principal.getName());
        if (activePath == null) {
            result.reject("error.path", "No tienes un trayecto activo para finalizar.");
            return "redirect:/home";
        }

        // Actualizar los campos del trayecto activo con los datos del formulario
        activePath.setFinalOdometer(path.getFinalOdometer());
        activePath.setObservations(path.getObservations());

        if (path.getFinalOdometer() != null && path.getFinalOdometer() > activePath.getInitialOdometer()) {
            activePath.setKilometers(path.getFinalOdometer() - activePath.getInitialOdometer());

            endTripValidator.validate(activePath, result);
            if (result.hasErrors()) {
                return "path/end";
            }

            pathService.endPath(activePath);
            return "redirect:/path/list";
        } else {
            result.rejectValue("finalOdometer", "error.path",
                    "El odómetro final debe ser mayor que el inicial (" + activePath.getInitialOdometer() + ")");
            return "path/end";
        }
    }


    @GetMapping("/listFromCar")
    public String showPathsFromCar(Model model, Principal principal) {

        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);

        return"path/listFromCar";
    }


    @GetMapping("/vehicle/{plate}")
    public String showPathsFromCar2(Model model, Principal principal, Pageable pageable, @PathVariable String plate) {

        Page<Path> pathsByVehicle = pathService.findByVehiclePlate(plate, pageable);

        model.addAttribute("pathsList", pathsByVehicle.getContent());
        model.addAttribute("page", pathsByVehicle);


        return"path/listFromCar2";
    }
}
