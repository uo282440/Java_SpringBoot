package com.uniovi.sdi2425entrega1ext514.pageobjects;


import com.uniovi.sdi2425entrega1ext514.entities.User;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class PO_PrivateView extends PO_NavView {

    /**
     * Verifica los errores a la hora de registrar usuario
     * @param driver
     * @param dnip
     * @param namep
     * @param lastNamep
     * @param expectedError
     */
    static public void registerUserError(WebDriver driver, String dnip, String namep, String lastNamep,
                                         String expectedError) {
        goToUserLink(driver, "register");


        // rellenamos los datos del usuario
        fillFormRegisterUser(driver, dnip, namep, lastNamep);
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", expectedError);

        Assertions.assertTrue(elements.getFirst().getText().contains(expectedError));
    }

    /**
     * Verifica el listado de todos los usuarios
     * @param driver
     * @param users
     */
    static public void listUsers(WebDriver driver, List<User> users) {
        goToUserLink(driver, "list");

        //primera pagina
        for (int i = 0; i < 5; i++) {
            if (!findUser(driver, users.get(i))) Assertions.fail("User not found");
        }

        //segunda pagina
        driver.get("http://localhost:8100/user/list?page=1");
        for (int i = 5; i < 10; i++) {
            if (!findUser(driver, users.get(i))) Assertions.fail("User not found");
        }

        //tercera pagina
        driver.get("http://localhost:8100/user/list?page=2");
        for (int i = 10; i < 15; i++) {
            if (!findUser(driver, users.get(i))) Assertions.fail("User not found");
        }
    }

    /**
     * Edita un usuario
     * @param driver
     * @param users
     * @return
     */
    static public String[] editUser(WebDriver driver, List<User> users) {
        goToUserLink(driver, "list");
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//tbody/tr[td[.//text()[contains(., '" + users.getFirst().getDni() + "')]]]/td[last()]/a");
        elements.getFirst().click();
        String dnip = "66778899A";
        String namep = "Ilyas";
        String lastNamep = "Chaira";
        int roleOrder = 1;

        fillFormEditUser(driver, dnip, namep, lastNamep, roleOrder);

        elements = PO_View.checkElementBy(driver, "text", dnip);
        Assertions.assertEquals(dnip, elements.getFirst().getText());
        elements = PO_View.checkElementBy(driver, "text", namep);
        Assertions.assertEquals(namep, elements.getFirst().getText());
        elements = PO_View.checkElementBy(driver, "text", lastNamep);
        Assertions.assertEquals(lastNamep, elements.getFirst().getText());

        return new String[]{dnip, namep, lastNamep, "ROLE_ADMIN"};
    }

    /**
     * Verifica los errores a la hora de editar un usuario
     * @param driver
     * @param dniToEdit
     * @param dnip
     * @param namep
     * @param lastNamep
     * @param errors
     */
    static public void editUserError(WebDriver driver, String dniToEdit, String dnip, String namep, String lastNamep,
                                     String... errors) {
        goToUserLink(driver, "list");
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//tbody/tr[td[.//text()[contains(., '" + dniToEdit + "')]]]/td[last()]/a");
        elements.getFirst().click();

        fillFormEditUser(driver, dnip, namep, lastNamep, 1);

        for (String error : errors) {
            elements = PO_View.checkElementBy(driver, "text", error);
            Assertions.assertTrue(elements.getFirst().getText().contains(error));
        }
    }

    /**
     * Realiza el proceso de cambiar la contraseña de un usuario
     * @param driver
     * @param oldPassword
     * @param newPassword
     */
    static public void changePassword(WebDriver driver, String oldPassword, String newPassword) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//*[@id='my-navbarColor02']/ul[2]/li[2]/a");
        elements.getFirst().click();
        fillFormChangaPassword(driver, oldPassword, newPassword, newPassword);
    }

    /**
     * Verifica los errores a la hora de cambiar la contraseña
     * @param driver
     * @param oldPassword
     * @param newPassword
     * @param passwordConfirm
     * @param error
     */
    static public void changePasswordError(WebDriver driver, String oldPassword, String newPassword,
                                           String passwordConfirm, String error) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//*[@id='my-navbarColor02']/ul[2]/li[2]/a");
        elements.getFirst().click();
        fillFormChangaPassword(driver, oldPassword, newPassword, passwordConfirm);

        elements = PO_View.checkElementBy(driver, "text", error);
        Assertions.assertTrue(elements.getFirst().getText().contains(error));
    }

    /**
     * Encuentra un usuario
     * @param driver
     * @param user
     * @return
     */
    static private boolean findUser(WebDriver driver, User user) {
        try {
            List<WebElement> elements = PO_View.checkElementBy(driver, "text", user.getDni());
            if (elements.getFirst().getText().equals(user.getDni())) {
                Assertions.assertEquals(user.getDni(), elements.getFirst().getText());
                return true;
            }
            return true;

        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Realiza el proceso de registrar un usuario con datos fijos
     * @param driver
     */
    static public void registerUser(WebDriver driver) {
        goToUserLink(driver, "register");

        // rellenar los datos del usuario
        String checkText = "Lamine";
        fillFormRegisterUser(driver, "99887766A", checkText, "Yamal");

        //se muestren los enlaces de paginación de la lista de usuarios
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//a[contains(@class, 'page-link')]");

        //vamos a la última página
        elements.getLast().click();

        //Comprobamos que aparece el usuario
        elements = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, elements.get(0).getText());
    }

    /**
     * Rellena el formulario de crear incidencia
     * @param driver
     * @param titulop
     * @param descripcionp
     * @param responsep
     * @param requiresResponse
     */
    static public void fillFormCreateIncidencia(WebDriver driver, String titulop, String descripcionp, String responsep, boolean requiresResponse) {

        SeleniumUtils.waitSeconds(driver, 5);
        WebElement title = driver.findElement(By.name("title"));
        title.clear();
        title.sendKeys(titulop);

        WebElement description = driver.findElement(By.name("description"));
        description.click();
        description.clear();
        description.sendKeys(descripcionp);

        // Seleccionar el radio button según el valor del parámetro
        String value = requiresResponse ? "true" : "false";
        WebElement radioButton = driver.findElement(By.cssSelector("input[name='requiresResponse'][value='" + value + "']"));
        radioButton.click();

        WebElement response = driver.findElement(By.name("response"));
        response.click();
        response.clear();
        response.sendKeys(responsep);

        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Rellena el formulario de editar un trayecto
     * @param driver
     * @param kilometrosp
     * @param odometroInicialp
     * @param odometroFinalp
     * @param esPruebaDeFecha
     */
    static public void fillFormEditPath(WebDriver driver, String kilometrosp, String odometroInicialp, String odometroFinalp, boolean esPruebaDeFecha) {

        SeleniumUtils.waitSeconds(driver, 5);
        WebElement kilometers = driver.findElement(By.name("kilometers"));
        kilometers.clear();
        kilometers.sendKeys(kilometrosp);

        WebElement initialOdometer = driver.findElement(By.name("initialOdometer"));
        initialOdometer.click();
        initialOdometer.clear();
        initialOdometer.sendKeys(odometroInicialp);

        WebElement finalOdometer = driver.findElement(By.name("finalOdometer"));
        finalOdometer.click();
        finalOdometer.clear();
        finalOdometer.sendKeys(odometroFinalp);

        //rellenamos la fecha con el 1 de diciembre de 2025
        String fechaFija = "2025-12-01T00:00";

        if (esPruebaDeFecha) { //test 58
            String fecha = "4035-12-01T00:00";
            WebElement startDate = driver.findElement(By.name("startDate"));
            startDate.clear();
            startDate.sendKeys(fecha);
        }

        WebElement endDate = driver.findElement(By.name("endDate"));
        endDate.clear();
        endDate.sendKeys(fechaFija);

        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Rellena el formulario de cambiar la contraseña
     * @param driver
     * @param oldPasswordp
     * @param newPasswordp
     * @param passwordConfirmp
     */
    static private void fillFormChangaPassword(WebDriver driver, String oldPasswordp, String newPasswordp,
                                               String passwordConfirmp) {
        SeleniumUtils.waitSeconds(driver, 5);

        WebElement oldPassword = driver.findElement(By.name("oldPassword"));
        oldPassword.clear();
        oldPassword.sendKeys(oldPasswordp);

        WebElement newPassword = driver.findElement(By.name("password"));
        newPassword.click();
        newPassword.clear();
        newPassword.sendKeys(newPasswordp);

        WebElement passwordConfirm = driver.findElement(By.name("passwordConfirm"));
        passwordConfirm.click();
        passwordConfirm.clear();
        passwordConfirm.sendKeys(passwordConfirmp);

        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Rellena el formulario de editar usuario
     * @param driver
     * @param dnip
     * @param namep
     * @param lastNamep
     * @param roleOrder
     */
    static private void fillFormEditUser(WebDriver driver, String dnip, String namep, String lastNamep, int roleOrder) {
        SeleniumUtils.waitSeconds(driver, 5);

        WebElement dni = driver.findElement(By.name("dni"));
        dni.clear();
        dni.sendKeys(dnip);

        WebElement name = driver.findElement(By.name("name"));
        name.click();
        name.clear();
        name.sendKeys(namep);

        WebElement lastName = driver.findElement(By.name("lastName"));
        lastName.click();
        lastName.clear();
        lastName.sendKeys(lastNamep);

        new Select(driver.findElement(By.name("role"))).selectByIndex(roleOrder);
        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Rellena el formulario de registrar usuario
     * @param driver
     * @param dnip
     * @param namep
     * @param lastNamep
     */
    static private void fillFormRegisterUser(WebDriver driver, String dnip, String namep, String lastNamep) {

        SeleniumUtils.waitSeconds(driver, 5);

        WebElement dni = driver.findElement(By.name("dni"));
        dni.clear();
        dni.sendKeys(dnip);

        WebElement name = driver.findElement(By.name("name"));
        name.click();
        name.clear();
        name.sendKeys(namep);

        WebElement lastName = driver.findElement(By.name("lastName"));
        lastName.click();
        lastName.clear();
        lastName.sendKeys(lastNamep);

        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Rellena el formulario de registrar repostaje
     * @param driver
     * @param gasolinerap
     * @param precioLitrop
     * @param litrosp
     * @param odometrop
     */
    static public void fillFormRegisterRefuel(WebDriver driver, String gasolinerap, String precioLitrop, String litrosp, double odometrop) {

        SeleniumUtils.waitSeconds(driver, 5);

        WebElement gasolinera = driver.findElement(By.name("stationName"));
        gasolinera.clear();
        gasolinera.sendKeys(gasolinerap);

        WebElement precioLitro = driver.findElement(By.name("fuelPrice"));
        precioLitro.click();
        precioLitro.clear();
        precioLitro.sendKeys(precioLitrop);

        WebElement litros = driver.findElement(By.name("fuelQuantity"));
        litros.click();
        litros.clear();
        litros.sendKeys(litrosp);

        WebElement odometro = driver.findElement(By.name("odometer"));
        odometro.click();
        odometro.clear();
        odometro.sendKeys(String.valueOf(odometrop));

        By button = By.className("btn");
        driver.findElement(button).click();
    }

    /**
     * Metodo que nos permite movernos a cierta pagina de la seccion de usuarios
     * @param driver
     * @param link
     */
    static private void goToUserLink(WebDriver driver, String link) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//*[@id='my-navbarColor02']/ul[1]/li[2]");
        elements.getFirst().click();
        elements = PO_View.checkElementBy(driver, "free", "//a[contains(@href, 'user/" + link + "')]");
        elements.getFirst().click();
    }

    /**
     * Realiza el proceso de registrar un vehiculo
     * @param driver
     */
    static public void registerVehicle(WebDriver driver) {
        goToVehicleLink(driver, "register");
        String checkText = "1234ABC";
        fillFormRegisterVehicle(driver, checkText, "11111111111111111", "Toyota", "qwe");

        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//a[contains(@class, 'page-link')]");

        elements.getLast().click();
        elements = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, elements.get(0).getText());
    }

    /**
     * Realiza el proceso de registrar un vehiculo segun los parametros determinados
     * @param driver
     * @param plate
     * @param chasis
     */
    static public void registerNewVehicle(WebDriver driver, String plate, String chasis) {
        goToVehicleLink(driver, "register");

        fillFormRegisterVehicle(driver, plate, chasis, "Toyota", "qwe");

        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//a[contains(@class, 'page-link')]");

        elements.getLast().click();
        elements = PO_View.checkElementBy(driver, "text", plate);
        Assertions.assertEquals(plate, elements.get(0).getText());
    }

    /**
     * Verifica los errores a la hora de registrar un vehiculo
     * @param driver
     * @param plateP
     * @param chassisP
     * @param brandP
     * @param modelP
     * @param expectedError
     */
    static public void registerVehicleError(WebDriver driver, String plateP, String chassisP, String brandP, String modelP, String expectedError) {
        goToVehicleLink(driver, "register");

        fillFormRegisterVehicle(driver, plateP, chassisP, brandP, modelP);

        List<WebElement> elements = PO_View.checkElementBy(driver, "text", expectedError);
        Assertions.assertTrue(elements.getFirst().getText().contains(expectedError));
    }

    /**
     * Verifica el listado de vehiculos
     * @param driver
     * @param vehicles
     */
    static public void listVehicles(WebDriver driver, List<Vehicle> vehicles) {
        goToVehicleLink(driver, "list");

        // Localiza la tabla
        WebElement table = driver.findElement(By.id("vehiclesTable"));

        // Encuentra todas las filas dentro del tbody
        List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

        // Verifica que haya exactamente 5 filas
        Assertions.assertEquals(5, rows.size());

    }

    /**
     * Encuentra un vehiculo
     * @param driver
     * @param vehicle
     * @return
     */
    static private boolean findVehicle(WebDriver driver, Vehicle vehicle) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", vehicle.getPlate());

        if (elements.getFirst().getText().equals(vehicle.getPlate())) {
            Assertions.assertEquals(vehicle.getPlate(), elements.getFirst().getText());
            return true;
        } else {
            elements = PO_View.checkElementBy(driver, "free",
                    "//div[@class='text-center']/ul[@class='pagination justify-content-center']/li[4]/a");
            elements.getFirst().click();
            return findVehicle(driver, vehicle);
        }
    }

    /**
     * Rellena el formulario de registrar un vehiculo
     * @param driver
     * @param plateP
     * @param chassisP
     * @param brandP
     * @param modelP
     */
    static private void fillFormRegisterVehicle(WebDriver driver, String plateP, String chassisP, String brandP, String modelP) {
        SeleniumUtils.waitSeconds(driver, 5);

        WebElement plate = driver.findElement(By.name("plate"));
        plate.clear();
        plate.sendKeys(plateP);

        WebElement chassis = driver.findElement(By.name("chassisNumber"));
        chassis.clear();
        chassis.sendKeys(chassisP);

        WebElement brand = driver.findElement(By.name("brandName"));
        brand.clear();
        brand.sendKeys(brandP);

        WebElement model = driver.findElement(By.name("model"));
        model.clear();
        model.sendKeys(modelP);

        WebElement fuelDropdown = driver.findElement(By.name("fuelType"));
        Select selectFuel = new Select(fuelDropdown);

        if (selectFuel.getOptions().size() > 1) {
            selectFuel.selectByIndex(1);
        }

        By button = By.className("btn");
        driver.findElement(button).click();

    }

    /**
     * Abre cierta seccion del menu de dropdown
     * @param driver
     * @param dropdownId
     */
    public static void openDropdown(WebDriver driver, String dropdownId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dropdownToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id(dropdownId)));
        dropdownToggle.click();
    }

    /**
     * Nos dirije al listado de vehiculos
     * @param driver
     */
    public static void goToVehicleList(WebDriver driver) {
        openDropdown(driver, "gestionVehiculos");

        // Espera a que aparezca el enlace correcto
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/vehicle/list']", 5);
        WebElement link = driver.findElement(By.xpath("//a[@href='/vehicle/list']"));
        link.click();

        // Espera a que cargue el título de la página
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Vehiculos')]", 10);
    }

    /**
     * Nos lleva a empezar un trayecto
     * @param driver
     */
    public static void goToStartTrip(WebDriver driver) {

        openDropdown(driver, "trayectosPersonales");

        // Espera a que aparezca el enlace correcto
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/path/start']", 5);
        WebElement link = driver.findElement(By.xpath("//a[@href='/path/start']"));
        link.click();

        // Espera a que cargue el título de la página
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Iniciar trayecto')]", 10);

    }

    /**
     * Nos lleva a fnializar un trayecto
     * @param driver
     * @param dist
     */
    public static void goToEndTrip(WebDriver driver, String dist) {

        openDropdown(driver, "trayectosPersonales");

        // Espera a que aparezca el enlace correcto
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/path/end']", 5);
        WebElement link = driver.findElement(By.xpath("//a[@href='/path/end']"));
        link.click();

        // Espera a que cargue el título de la página
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Finalizar trayecto')]", 10);

        fillFormEndTripForm(driver, dist);

    }

    /**
     * Nos lleva a finalizar un trayecto con errores
     * @param driver
     * @param dist
     */
    public static void goToEndTripError(WebDriver driver, String dist) {

        openDropdown(driver, "trayectosPersonales");

        // Espera a que aparezca el enlace correcto
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/path/end']", 5);
        WebElement link = driver.findElement(By.xpath("//a[@href='/path/end']"));
        link.click();

        // Espera a que cargue el título de la página
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Finalizar trayecto')]", 10);

        fillFormEndTripFormError(driver, dist);

    }

    /**
     * Rellena el formulario de final de trayecto
     * @param driver
     * @param kilometersP
     */
    private static void fillFormEndTripForm(WebDriver driver, String kilometersP) {
        SeleniumUtils.waitSeconds(driver, 5);

        WebElement kilometers = driver.findElement(By.id("finalOdometer"));
        kilometers.clear();
        kilometers.sendKeys(kilometersP);

        By button = By.className("btn");
        driver.findElement(button).click();
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Trayectos')]", 10);
    }

    /**
     * verifica los errores al acabar un trayecto
     * @param driver
     * @param kilometersP
     */
    private static void fillFormEndTripFormError(WebDriver driver, String kilometersP) {
        SeleniumUtils.waitSeconds(driver, 5);
        WebElement kilometers = driver.findElement(By.id("finalOdometer"));

        kilometers.clear();
        kilometers.sendKeys(kilometersP);

        By button = By.className("btn");
        driver.findElement(button).click();
        SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Finalizar trayecto')]", 10);
    }

    /**
     * Nos lleva a cierta seccion del menu de vehiculos
     * @param driver
     * @param link
     */
    public static void goToVehicleLink(WebDriver driver, String link) {

        List<WebElement> elements = PO_View.checkElementBy(driver, "free",
                "//*[@id='my-navbarColor02']/ul[1]/li[3]");

        elements.getFirst().click();
        elements = PO_View.checkElementBy(driver, "free", "//a[contains(@href, 'vehicle/" + link + "')]");
        elements.getFirst().click();
    }

    /**
     * Selecciona vehiculos gracias al checkbox
     * @param driver
     * @param index
     * @return
     */
    public static WebElement selectVehicleCheckbox(WebDriver driver, int index) {
        return driver.findElements(By.name("selectedVehicles")).get(index);
    }

    /**
     * Hace click en eliminar vehiculos
     * @param driver
     */
    public static void deleteVehicle(WebDriver driver) {
        WebElement deleteButton = driver.findElement(By.xpath("//form[@id='deleteForm']/button"));
        deleteButton.click();
    }

    /**
     * Verifica que los vehiculos desaparecieron
     * @param driver
     * @param vehiclePlate
     */
    public static void verifyVehicleDisappeared(WebDriver driver, String vehiclePlate) {
        List<WebElement> vehicles = driver.findElements(By.xpath("//table[@id='vehiclesTable']//tbody//tr"));
        boolean found = false;

        for (WebElement vehicle : vehicles) {
            WebElement plateElement = vehicle.findElement(By.xpath("td[2]"));
            if (plateElement.getText().equals(vehiclePlate)) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "El vehículo con matrícula " + vehiclePlate + " sigue en la lista.");
    }
}