package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;
    private final UsersService usersService;
    private final HttpSession httpSession;
    private final VehicleService vehicleService;




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
}
