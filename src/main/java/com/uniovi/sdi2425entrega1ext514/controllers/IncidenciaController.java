package com.uniovi.sdi2425entrega1ext514.controllers;



import com.uniovi.sdi2425entrega1ext514.entities.Incidencia;
import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.services.IncidenciaService;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IncidenciaController {

    private final IncidenciaService incidenciaService;
    private final PathService pathService;
    private final UsersService usersService;
    private final VehicleService vehicleService;


    public IncidenciaController(IncidenciaService incidenciaService, PathService pathService, UsersService usersService, VehicleService vehicleService) {
        this.incidenciaService = incidenciaService;
        this.pathService = pathService;
        this.usersService = usersService;
        this.vehicleService = vehicleService;
    }



    @RequestMapping("/incidencia/add")
    public String addIncidencia(Model model, Principal principal) {

        String userDni = principal.getName();
        Path activePath = pathService.getActivePathForUser(userDni);

        if(activePath == null) {
            model.addAttribute("error", "No tienes un trayecto en curso para registrar una incidencia.");
            return "redirect:/home";
        }

        return "incidencia/addIncidencia";
    }

    @RequestMapping(value = "/incidencia/register", method = RequestMethod.POST)
    public String registerIncidencia(Incidencia incidencia, Principal principal) {
        String userDni = principal.getName();
        User user = usersService.getUserByDni(userDni);

        incidencia.setUser(user);

        Path activePath = pathService.getActivePathForUser(userDni);
        Vehicle vechicle = vehicleService.findByPlate(activePath.getVehicleRegistration());

        incidencia.setVehicle(vechicle);

        incidencia.setDate(LocalDateTime.now());

        if (incidencia.getRequiresResponse()) {
            incidencia.setState("REGISTRADA");
        } else {
            incidencia.setState("SIN_RESPUESTA");
        }

        incidenciaService.save(incidencia);

        System.out.println(incidencia.toString());

        return "redirect:/incidencia/list";
    }



    @RequestMapping("/incidencia/list")
    public String listIncidencia(Model model, Principal principal) {

        return "incidencia/list";
    }



    @RequestMapping("/incidencia/listType")
    public String listTypeIncidencia(Model model, Principal principal, @RequestParam String typeIncidencia) {
        String userDni = principal.getName();

        List<Incidencia> incidencias;

        //añadir al modelo las inbcidencias de ese tipo y mostrarlas en el mismo html
        incidencias = incidenciaService.findByStateAndDNI(userDni, typeIncidencia);

        model.addAttribute("incidencias", incidencias);


        return "incidencia/list";
    }



    @RequestMapping("/incidencia/listAdmin")
    public String listIncidenciaAdmin(Model model, Principal principal) {


        List<Incidencia> incidencias = incidenciaService.findAllList();


        List<Incidencia> ordenadas = new ArrayList<Incidencia>();


        //“REGISTRADA” > “LEIDA” > “EN PROCESO” > “RESUELTA”.

        // 1º meto las Registradas
        for (Incidencia incidencia : incidencias) {
            if (incidencia.getState().equals("REGISTRADA")) {
                ordenadas.add(incidencia);
                //incidencias.remove(incidencia);
            }
        }

        // 2º meto las lEIDAS
        for (Incidencia incidencia : incidencias) {
            if (incidencia.getState().equals("LEIDA")) {
                ordenadas.add(incidencia);
                //incidencias.remove(incidencia);
            }
        }

        // 3º meto las en proceso
        for (Incidencia incidencia : incidencias) {
            if (incidencia.getState().equals("EN PROCESO")) {
                ordenadas.add(incidencia);
                //incidencias.remove(incidencia);
            }
        }

        // 4º meto las RESUELTA
        for (Incidencia incidencia : incidencias) {
            if (incidencia.getState().equals("TERMINADA")) {
                ordenadas.add(incidencia);
                //incidencias.remove(incidencia);
            }
        }

        model.addAttribute("incidencias", ordenadas);


        return "incidencia/listAdmin";
    }


    @RequestMapping("/incidencia/listDetails/{id}")
    public String listIncidenciaDetails(Model model, Principal principal, @PathVariable Long id) {


        Incidencia inci = incidenciaService.findOne(id);

        if (inci.getState().equals("REGISTRADA")) {
            inci.setState("LEIDA");
        }

        incidenciaService.updateOne(id, inci.getState());

        model.addAttribute("incidencia", inci);


        return "incidencia/details";
    }


    @RequestMapping(value = "/incidencia/enProceso/{id}", method = RequestMethod.POST)
    public String procesarIncidencia(@PathVariable Long id, Model model, Principal principal) {
        Incidencia inci = incidenciaService.findOne(id);

        if (inci.getState().equals("LEIDA")) {

            inci.setState("EN PROCESO");

            incidenciaService.updateOne(id, inci.getState());

            model.addAttribute("incidencia", inci);

            return "incidencia/details";

        } else { // si no esta en proceso no se puede terminar

            model.addAttribute("incidencia", inci);
            model.addAttribute("mensajeError", "No se puede procesar una incidencia si no está en estado LEIDA.");
            return "incidencia/details";
        }
    }


    @RequestMapping(value = "/incidencia/terminar/{id}", method = RequestMethod.POST)
    public String terminarIncidencia(@PathVariable Long id, Model model, Principal principal) {
        Incidencia inci = incidenciaService.findOne(id);

        if (inci.getState().equals("EN PROCESO")) {

            inci.setState("TERMINADA");

            incidenciaService.updateOne(id, inci.getState());

            model.addAttribute("incidencia", inci);

            return "incidencia/details";

        } else { // si no esta en proceso no se puede terminar

            model.addAttribute("incidencia", inci);
            model.addAttribute("mensajeError", "No se puede terminar una incidencia si no está EN PROCESO.");
            return "incidencia/details";
        }
    }

}
