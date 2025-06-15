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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Arrays;

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

    /**
     * Metodo para la identificacion del usuario que está en sesion, utilizado para los logs
     * @return
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Metodo GET que renderiza en html del login
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "user/login";
    }

    /**
     * Metodo GET que renderiza el html de listar usuarios
     * @param model
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public String listUsers(Model model, Pageable pageable) {

        Page<User> users = usersService.getUsers(pageable);
        model.addAttribute("usersList", users.getContent());
        model.addAttribute("page", users);

        logService.saveLog(getCurrentUsername(), "PET", "/user/list");

        return "user/list";
    }

    /**
     * Metodo GET que renderiza el html de registar usuarios
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    public String register(Model model) {

        User user = new User();

        user.setPassword(usersService.generateUserPassword());
        model.addAttribute("user", user);

        return "user/register";
    }

    /**
     * Metodo POST para registrar usuarios
     * @param user
     * @param result
     * @return
     */
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


    /**
     * Metodo GET que renderiza el html para editar usuarios
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/user/edit/{id}",  method = RequestMethod.GET)
    public String edit(Model model, @PathVariable Long id) {

        User user = usersService.getUser(id);

        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(rolesService.getRoles()));

        return "user/edit";
    }


    /**
     * Metodo POST que permite editar un usuario
     * @param id
     * @param user
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable Long id, @ModelAttribute User user, BindingResult result, Model model) {

        User originalUser = usersService.getUser(id);

        editFormValidator.validate(user, result, originalUser.getDni());
        if(result.hasErrors()) {
            model.addAttribute("roles", Arrays.asList(rolesService.getRoles()));
            return "user/edit";
        }

        originalUser.setDni(user.getDni());
        originalUser.setName(user.getName());
        originalUser.setLastName(user.getLastName());
        originalUser.setRole(user.getRole());
        usersService.editUser(originalUser);

        return "redirect:/user/list";
    }


    /**
     * Metodo POST que nos permite modificar la contraseña de un usuario
     * @param user
     * @param principal
     * @param result
     * @return
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(@Validated User user, Principal principal, BindingResult result) {

        passwordChangeFormValidator.validate(user, result, principal);
        if(result.hasErrors()) {
            return "user/passwordChange";
        }

        String dni = principal.getName(); // DNI es el name de la autenticación
        User activeUser = usersService.getUserByDni(dni);
        activeUser.setPassword(user.getPassword());
        usersService.changePassword(activeUser);

        logService.saveLog(dni, "PET", "/changePassword");

        return "redirect:/home";
    }


    /**
     * Metodo GET que renderiza el html para cambiar la contraseña de un usuario
     * @param model
     * @return
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(Model model) {

        model.addAttribute("user", new User());

        return "user/passwordChange";
    }


    /**
     * Metodo GET que renderia el html de home
     * @param model
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String goHome(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String dni = auth.getName();

        User activeUser = usersService.getUserByDni(dni);

        model.addAttribute("user", activeUser);

        logService.saveLog(dni, "PET", "/home");

        return "home";
    }


}
