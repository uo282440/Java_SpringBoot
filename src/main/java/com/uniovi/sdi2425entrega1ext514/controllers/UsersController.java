package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.services.LogService;
import com.uniovi.sdi2425entrega1ext514.services.RolesService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.validators.EditFormValidator;
import com.uniovi.sdi2425entrega1ext514.validators.PasswordChangeFormValidator;
import com.uniovi.sdi2425entrega1ext514.validators.RegisterFormValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController {

    private final UsersService usersService;
    private final RolesService rolesService;
    private final LogService logService;

    private final RegisterFormValidator registerFormValidator;
    private final PasswordChangeFormValidator passwordChangeFormValidator;
    private final EditFormValidator editFormValidator;


    public UsersController(UsersService usersService, RegisterFormValidator registerFormValidator, PasswordChangeFormValidator passwordChangeFormValidator,
                           RolesService rolesService, EditFormValidator editFormValidator, LogService logService) {
        this.usersService = usersService;
        this.registerFormValidator = registerFormValidator;
        this.passwordChangeFormValidator = passwordChangeFormValidator;
        this.editFormValidator = editFormValidator;
        this.rolesService = rolesService;
        this.logService = logService;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "user/login";
    }

    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public String listUsers(Model model, Pageable pageable) {

        Page<User> users = usersService.getUsers(pageable);
        model.addAttribute("usersList", users.getContent());
        model.addAttribute("page", users);

        logService.saveLog(getCurrentUsername(), "PET", "/user/list");

        return "user/list";
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    public String register(Model model) {

        User user = new User();

        user.setPassword(usersService.generateUserPassword());
        model.addAttribute("user", user);

        return "user/register";
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public String register(@Validated User user, BindingResult result) {

        registerFormValidator.validate(user, result);
        if(result.hasErrors()) {
            return "user/register";
        }

        user.setRole(rolesService.getRoles()[0]);
        usersService.addUser(user);

        String username = getCurrentUsername();
        logService.saveLog(username, "PET", "/user/register");
        logService.saveLog(username, "ALTA", "Usuario registrado: " + user.getDni());

        return "redirect:/user/list";
    }



}
