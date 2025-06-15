package com.uniovi.sdi2425entrega1ext514.validators;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EndTripValidator implements Validator {

    @Autowired
    private PathService pathService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Path.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Path path = (Path) target;

        if (path.getId() == null) {
            errors.reject("path.id.required", "No se ha indicado el trayecto a finalizar.");
            return;
        }

        if (errors.hasFieldErrors("finalOdometer")) {

            return;
        }

    }

}
