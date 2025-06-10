package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class InsertSampleDataService {

    private final UsersService usersService;
    private final RolesService rolesService;

    private final VehicleService vehicleService;
    private final PathService pathService;
    private final RefuelService refuelService;

    public InsertSampleDataService(UsersService usersService, RolesService rolesService, VehicleService vehicleService, PathService pathService, RefuelService refuelService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
        this.vehicleService = vehicleService;
        this.pathService = pathService;
        this.refuelService = refuelService;
    }

    @PostConstruct
    public void init() {
        User user1 = new User("99999990A", "Pedro", "Díaz");
        user1.setPassword("123456");
        user1.setRole(rolesService.getRoles()[0]);

        User user2 = new User("12345678Z", "Lucas", "Núñez");
        user2.setPassword("@Dm1n1str@D0r");
        user2.setRole(rolesService.getRoles()[1]);

        User user3 = new User("99999992C", "María", "Rodríguez");
        user3.setPassword("123456");
        user3.setRole(rolesService.getRoles()[0]);

        User user4 = new User("99999992D", "PABLO", "Rodríguez");
        user4.setPassword("123456");
        user4.setRole(rolesService.getRoles()[0]);

        User user5 = new User("99999992E", "Joaquin", "Rodríguez");
        user5.setPassword("123456");
        user5.setRole(rolesService.getRoles()[0]);


        usersService.addUser(user1);
        usersService.addUser(user2);
        usersService.addUser(user3);
        usersService.addUser(user4);
        usersService.addUser(user5);


        // Creación de vehículos
        String[] matriculas = {
                "1111ZZZ", "1234BCD", "O1234AB", "5678EFG", "2345XYZ",
                "A1234BCD", "1234BCM", "O1234AM", "5678EFM", "2345XYM",
                "1234ZCM", "1234BMZ", "1234ZZJ", "5678EZZ", "2345XZZ", "1335BCD"
        };

        Vehicle[] vehicles = {
                new Vehicle("1111ZZZ", "CHASIS", "Alfa", "Romeo", Vehicle.FUEL_TYPES.DIESEL, 4578),
                new Vehicle("1234BCD", "CHASIS12345678901234", "Alfa", "Romeo", Vehicle.FUEL_TYPES.DIESEL, 4578),
                new Vehicle("O1234AB", "CHASIS23456789012345", "Toyota", "Corolla", Vehicle.FUEL_TYPES.GASOLINA, 12345),
                new Vehicle("5678EFG", "CHASIS34567890123456", "Honda", "Civic", Vehicle.FUEL_TYPES.HIBRIDO, 67890),
                new Vehicle("2345XYZ", "CHASIS45678901234567", "Ford", "Focus", Vehicle.FUEL_TYPES.ELECTRICO, 23456),
                new Vehicle("A1234BCD", "CHASIS56789012345678", "Volkswagen", "Golf", Vehicle.FUEL_TYPES.DIESEL, 78901),
                new Vehicle("1234BCM", "CHASIS12345678901231", "Alfa", "Romeo", Vehicle.FUEL_TYPES.DIESEL, 4578),
                new Vehicle("O1234AM", "CHASIS23456789012311", "Toyota", "Corolla", Vehicle.FUEL_TYPES.GASOLINA, 12345),
                new Vehicle("5678EFM", "CHASIS34567890123111", "Honda", "Civic", Vehicle.FUEL_TYPES.HIBRIDO, 67890),
                new Vehicle("2345XYM", "CHASIS45678901231111", "Ford", "Focus", Vehicle.FUEL_TYPES.ELECTRICO, 23456),
                new Vehicle("1234ZCM", "CHASIS567890111111", "Volkswagen", "Golf", Vehicle.FUEL_TYPES.DIESEL, 78901),
                new Vehicle("1234BMZ", "CHASIS12345678901230", "Alfa", "Romeo", Vehicle.FUEL_TYPES.DIESEL, 4578),
                new Vehicle("1234ZZJ", "CHASIS23456789012310", "Toyota", "Corolla", Vehicle.FUEL_TYPES.GASOLINA, 12345),
                new Vehicle("5678EZZ", "CHASIS34567890123110", "Honda", "Civic", Vehicle.FUEL_TYPES.HIBRIDO, 67890),
                new Vehicle("2345XZZ", "CHASIS45678901231110", "Ford", "Focus", Vehicle.FUEL_TYPES.ELECTRICO, 23456),
                new Vehicle("1335BCD", "12345678901234111", "Alfa", "Romeo", Vehicle.FUEL_TYPES.DIESEL, 4578)
        };

        for (Vehicle v : vehicles) {
            vehicleService.addVehicle(v);
        }

        // Asignar 15 trayectos por usuario
        User[] users = {user1, user2, user3, user4, user5};
        int pathId = 0;
        for (User user : users) {
            for (int i = 0; i < 15; i++) {

                String matricula = matriculas[i % matriculas.length];
                int kmStart = 1000 + pathId * 10;
                int kmEnd = kmStart + 10;
                double fuelUsed = 5 + (i % 5); // Combustible usado variable

                Date date = new Date(System.currentTimeMillis() - (long) (i * 86400000L));
                LocalDateTime dateTime = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                Path path = new Path(dateTime, 1.2, kmStart, kmEnd, matricula, fuelUsed, user);

                path.setUserDni(user.getDni());
                pathService.addPath(path);
                pathId++;
            }
        }

        Refuel r1 = new Refuel("RedSol", 1.40, 30, 2142, new Date(), "", vehicles[0]);
        refuelService.addRefuel(r1);
    }
}
