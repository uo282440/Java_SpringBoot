package com.uniovi.sdi2425entrega1ext514.validators;


import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class StartTripValidator implements Validator {

    private final PathService pathService;

    @Autowired
    public StartTripValidator(PathService pathService) {
        this.pathService = pathService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Path.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Path path = (Path) target;


        if (path.getUserDni() == null || path.getUserDni().trim().isEmpty()) {
            errors.rejectValue("userDni", "error.userDni.required", "El DNI del empleado es obligatorio.");
        } else if (pathService.getActivePathForUser(path.getUserDni()) != null) {

            errors.reject("error.activePath", "El empleado ya tiene un trayecto activo, no puede iniciar otro.");
        }


        if (path.getVehicleRegistration() == null || path.getVehicleRegistration().trim().isEmpty()) {
            errors.rejectValue("vehicleRegistration", "error.vehicleRegistration.required", "La matrícula del vehículo es obligatoria.");
        } else if (pathService.hasActivePathForVehicle(path.getVehicleRegistration())) {

            errors.rejectValue("vehicleRegistration", "error.vehicle.inUse", "El vehículo ya está en uso por otro empleado.");
        }
    }
}
