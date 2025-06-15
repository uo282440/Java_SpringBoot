package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.LogService;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.validators.EndTripValidator;
import com.uniovi.sdi2425entrega1ext514.validators.StartTripValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final LogService logService;

    @Autowired
    private EndTripValidator endTripValidator;
    @Autowired
    private StartTripValidator startTripValidator;


    public PathController(PathService pathService, UsersService usersService, HttpSession httpSession, VehicleService vehicleService, LogService logService) {
        this.pathService = pathService;
        this.usersService = usersService;
        this.httpSession = httpSession;
        this.vehicleService = vehicleService;
        this.logService = logService;
    }

    /**
     * Metodo que nos devuelve la identificacion del usuario en sesion, utilizado para añadir logs
     * @return
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    /**
     * Metodo GET que renderiza la lista de trayectos
     * @param model
     * @param pageable
     * @param principal
     * @return
     */
    @GetMapping("/list")
    public String getList(Model model, Pageable pageable, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/list");

        Pageable pageableWithSize = PageRequest.of(pageable.getPageNumber(), 5);

        String dni = principal.getName();
        User user = usersService.getUserByDni(dni);

        Page<Path> paths = pathService.getPathsForUser(pageable, user);

        model.addAttribute("pathList", paths.getContent());
        model.addAttribute("page", paths);

        return "path/list";
    }

    /**
     * Metodo GET que renderiza el html para iniciar un trayecto
     * @param model
     * @return
     */
    @GetMapping("/start")
    public String showStartForm(Model model) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/start");

        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        model.addAttribute("availableVehicles", availableVehicles);

        LocalDateTime localDateTime = LocalDateTime.now();
        model.addAttribute("path", new Path());
        return "path/start";
    }

    /**
     * Metodo POST para inciar un trayecto
     * @param path
     * @param vehiclePlate
     * @param result
     * @param principal
     * @param model
     * @return
     */
    @PostMapping("/start")
    public String startPath(@ModelAttribute("path") Path path, @RequestParam("vehicleRegistration") String vehiclePlate, BindingResult result, Principal principal,
                            Model model) {

        logService.saveLog(getCurrentUsername(), "ALTA", "/path/start");

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

    /**
     * Metodo GET para renderizar el formulario que permite finalizar un trayecto
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/end")
    public String showEndForm(Model model, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/end");

        Path activePath = pathService.getActivePathForUser(principal.getName());
        if (activePath == null) {
            model.addAttribute("error", "No tienes un trayecto en curso para finalizar.");
            return "redirect:/home";
        }
        model.addAttribute("activePath", activePath);
        return "path/end";
    }


    /**
     * Metodo POST que finaliza un trayecto activo
     * @param path
     * @param result
     * @param principal
     * @param model
     * @return
     */
    @PostMapping("/end")
    public String endPath(@ModelAttribute("activePath") Path path,
                          BindingResult result, Principal principal, Model model) {

        logService.saveLog(getCurrentUsername(), "ALTA", "/path/end");

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

    /**
     * Metodo GET que renderiza un html donde se muestran los vehiculos existentes, para que despues el usuario
     * decida de cual de ellos quiere ver sus trayectos
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/listFromCar")
    public String showPathsFromCar(Model model, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/listFromCar");

        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);

        return"path/listFromCar";
    }


    /**
     * Metodo GET que renderiza un html con los trayectos de cierto vehiculo
     * @param model
     * @param principal
     * @param pageable
     * @param plate
     * @return
     */
    @GetMapping("/vehicle/{plate}")
    public String showPathsFromCar2(Model model, Principal principal, Pageable pageable, @PathVariable String plate) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/vehicle/" + plate);

        Page<Path> pathsByVehicle = pathService.findByVehiclePlate(plate, pageable);

        model.addAttribute("pathsList", pathsByVehicle.getContent());
        model.addAttribute("page", pathsByVehicle);

        return"path/listFromCar2";
    }

    /**
     * Metodo GET que renderiza un html donde se muestran los vehiculos del sistema, posteriormente el usuaria elegira
     * de que vehiculo quiere ver sus trayectos
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/listAdmin")
    public String editPathsFromCar(Model model, Principal principal) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/listAdmin");

        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);

        return"path/listAdmin";
    }

    /**
     * Metodo GET que nos permite renderizar un html con todos los trayectos de cierto vehiculo para que el administrador
     *  pueda editarlos
     * @param model
     * @param principal
     * @param plate
     * @return
     */
    @GetMapping("/show")
    public String showPathsFromCar(Model model, Principal principal,  @RequestParam String plate) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/show");

        List<Path> allPathsFromVehicle = pathService.getPathsByVehicle(plate);
        model.addAttribute("pathsList", allPathsFromVehicle);

        List<Vehicle> allVehicles = vehicleService.findAll();
        model.addAttribute("vehicles", allVehicles);


        return"path/listAdmin";
    }

    /**
     * Metodo GET que renderiza un html para editar el trayecto de cierto vehiculo
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {

        logService.saveLog(getCurrentUsername(), "PET", "/path/edit/" + id);

        Path path = pathService.getPath(id);
        model.addAttribute("path", path);
        return "path/edit";
    }

    /**
     * Metodo POST que nos permite editar un trayecto
     * @param id
     * @param path
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable Long id, @ModelAttribute Path path, BindingResult result, Model model) {

        logService.saveLog(getCurrentUsername(), "ALTA", "/path/edit/" + id);

        Path originalPath = pathService.getPath(id);

        if (path.getKilometers() <= 0 || path.getInitialOdometer() > path.getFinalOdometer()
                || path.getStartDate().isAfter(path.getEndDate())) {

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

    /**
     * Metodo utilizado para el formateo de fechas en los trayectos
     * @param binder
     */
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
