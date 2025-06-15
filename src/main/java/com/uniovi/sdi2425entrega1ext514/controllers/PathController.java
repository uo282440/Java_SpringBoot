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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        Vehicle v = vehicleService.findByPlate(vehiclePlate);
        v.setFree(false);
        vehicleService.updateState(vehiclePlate, false); //el coche ya no esta libre (isFree a false)

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

            Vehicle v = vehicleService.findByPlate(activePath.getVehicleRegistration());
            v.setFree(false);
            vehicleService.updateState(v.getPlate(), true); //el coche ya esta libre (isFree a true)

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


    @GetMapping("/listAdmin")
    public String editPathsFromCar(Model model, Principal principal) {

        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);


        return"path/listAdmin";
    }

    @GetMapping("/show")
    public String showPathsFromCar(Model model, Principal principal,  @RequestParam String plate) {


        List<Path> allPathsFromVehicle = pathService.getPathsByVehicle(plate);
        model.addAttribute("pathsList", allPathsFromVehicle);


        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);


        return"path/listAdmin";
    }


    @RequestMapping(value = "/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {

        Path path = pathService.getPath(id);
        model.addAttribute("path", path);
        return "path/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable Long id, @ModelAttribute Path path, BindingResult result, Model model) {

        Path originalPath = pathService.getPath(id);

        //añadir validacion
        if (path.getKilometers() <= 0 || path.getInitialOdometer() > path.getFinalOdometer()
                || path.getStartDate().isAfter(path.getEndDate())) {

            System.out.println("Hubo un error al editar el path");


            Path path2 = pathService.getPath(id);
            model.addAttribute("path", path2);
            return "redirect:/path/edit/" + id;
        }

        originalPath.setStartDate(path.getStartDate());
        originalPath.setEndDate(path.getEndDate());
        originalPath.setFinalOdometer(path.getFinalOdometer());
        originalPath.setInitialOdometer(path.getInitialOdometer());
        originalPath.setKilometers(path.getKilometers());

        pathService.editPath(originalPath);
        return "redirect:/path/listAdmin";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) : "";
            }
        });
    }
}
