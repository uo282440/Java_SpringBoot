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

        // 1. Comprobar que se ha indicado el ID del trayecto
        if (path.getId() == null) {
            errors.reject("path.id.required", "No se ha indicado el trayecto a finalizar.");
            return;
        }

        // 4. Ver si hay un error de conversión en finalConsumption (por ejemplo, campo vacío o texto no numérico)
        if (errors.hasFieldErrors("finalOdometer")) {
            // Con esto evitamos hacer más validaciones; el error de conversión ya está registrado
            return;
        }


    }

}
