package com.uniovi.sdi2425entrega1ext514.validators;


import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.security.Principal;

@Component
public class PasswordChangeFormValidator implements Validator {

    private final UsersService usersService;
    private User activeUser;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordChangeFormValidator(UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }


    public void validate(Object target, Errors errors, Principal principal) {
        String dni = principal.getName(); // DNI es el name de la autenticación
        activeUser = usersService.getUserByDni(dni);
        validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "Error.empty");
        if (!bCryptPasswordEncoder.matches(user.getOldPassword(), activeUser.getPassword())) {
            errors.rejectValue("oldPassword", "Error.password.change.incorrect");
        } else if(!user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("password", "Error.password.change.different");
            errors.rejectValue("passwordConfirm", "Error.password.change.different");
        } else if(!usersService.checkValidPassword(user.getPassword())) {
            errors.rejectValue("password", "Error.password.change.weak");
        }
    }
}