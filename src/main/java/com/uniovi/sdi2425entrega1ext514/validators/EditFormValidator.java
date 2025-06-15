package com.uniovi.sdi2425entrega1ext514.validators;


import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class EditFormValidator implements Validator {

    private final UsersService usersService;
    private String oldDni;

    public EditFormValidator(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    public void validate(Object target, Errors errors, String oldDni) {
        this.oldDni = oldDni;
        validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {

        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");

        String trimmed = user.getDni().trim();
        boolean valid = true;
        if(user.getDni().length() > trimmed.length()) {
            errors.rejectValue("dni", "Error.register.dni.spaces");
            valid = false;
        } else if (user.getDni().length() != 9) {
            errors.rejectValue("dni", "Error.register.dni.format");
            valid = false;
        } else {
            for (int i = 0; i < 8; i++) {
                if (!Character.isDigit(user.getDni().charAt(i))) {
                    errors.rejectValue("dni", "Error.register.dni.format");
                    valid = false;
                }
            }
        }

        if (valid && !user.getDni().equals(oldDni) && usersService.getUserByDni(user.getDni()) != null) {
            errors.rejectValue("dni", "Error.register.dni.duplicate");
        }

        trimmed = user.getName().trim();
        if (user.getName().length() > trimmed.length()) {
            errors.rejectValue("name", "Error.register.name.spaces");
        }

        trimmed = user.getLastName().trim();
        if (user.getLastName().length() > trimmed.length()) {
            errors.rejectValue("lastName", "Error.register.lastName.spaces");
        }
    }
}