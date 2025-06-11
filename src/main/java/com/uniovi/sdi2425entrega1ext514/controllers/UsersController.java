package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.services.LogService;
import com.uniovi.sdi2425entrega1ext514.services.RolesService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.validators.EditFormValidator;
import com.uniovi.sdi2425entrega1ext514.validators.PasswordChangeFormValidator;
import com.uniovi.sdi2425entrega1ext514.validators.RegisterFormValidator;
import org.springframework.stereotype.Controller;
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


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "user/login";
    }
}
