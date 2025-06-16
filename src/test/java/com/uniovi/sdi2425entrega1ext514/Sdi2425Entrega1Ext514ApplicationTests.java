package com.uniovi.sdi2425entrega1ext514;

import com.uniovi.sdi2425entrega1ext514.entities.Path;
import com.uniovi.sdi2425entrega1ext514.entities.Refuel;
import com.uniovi.sdi2425entrega1ext514.entities.Vehicle;
import com.uniovi.sdi2425entrega1ext514.pageobjects.*;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.RefuelService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import com.uniovi.sdi2425entrega1ext514.util.SeleniumUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2425Entrega1Ext514ApplicationTests {

	static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckodriver = "C:\\geckodriver.exe";

	static WebDriver driver = getDriver(PathFirefox, Geckodriver);
	static String URL = "http://localhost:8100";

	@Autowired
	private PathService pathService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private RefuelService refuelService;
	@Autowired
	private VehicleService vehicleService;

	public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckodriver);
		driver = new FirefoxDriver();
		return driver;
	}


	@BeforeEach
	public void setUp() {
		driver.navigate().to(URL);
	}

	//borrar las cookies del navegador
	@AfterEach
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	@AfterAll
	static public void end() {
		driver.quit();
	}


	@Test
	@Order(1)
	void Prueba1() {
		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "12345678Z", "@Dm1n1str@D0r");

		// Comprobamos que estamos en la URL que contiene "user/list"
		String currentUrl = driver.getCurrentUrl();
		Assertions.assertTrue(currentUrl.contains("user/list"), "La URL no contiene 'path/list'. URL actual: " + currentUrl);

	}

	/**
    [Prueba2] Inicio de sesión con datos válidos (empleado estándar)
     */
	@Test
	@Order(2)
	void Prueba2() {

		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "99999990A", "123456");


		// Comprobamos que estamos en la URL que contiene "path/list"
		String currentUrl = driver.getCurrentUrl();
		Assertions.assertTrue(currentUrl.contains("path/list"), "La URL no contiene 'path/list'. URL actual: " + currentUrl);

	}

	/**
    [Prueba3] Inicio de sesión con datos inválidos (empleado estándar, campo dni y contraseña vacíos).
     */
	@Test
	@Order(3)
	void Prueba3() {
		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "99999990A", "zzz");

		//Comprobamos que seguimos en la pagina de identificacion
		String checkText = PO_HomeView.getP().getString("login.message", PO_Properties.getSPANISH());
		List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
		Assertions.assertEquals(checkText, result.get(0).getText());
	}

	/**
    [Prueba4] Inicio de sesión con datos válidos (empleado estándar, dni existente, pero contraseña
        incorrecta).
     */
	@Test
	@Order(4)
	void Prueba4() {
		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "", "");

		//Comprobamos que seguimos en la pagina de identificacion
		String checkText = PO_HomeView.getP().getString("login.message", PO_Properties.getSPANISH());
		List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
		Assertions.assertEquals(checkText, result.get(0).getText());
	}

	/**
    [Prueba5] Hacer clic en la opción de salir de sesión y comprobar que se muestra el mensaje “Ha cerrado
        sesión correctamente” y se redirige a la página de inicio de sesión (Login).
     */
	@Test
	@Order(5)
	void Prueba5() {
		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "12345678Z", "@Dm1n1str@D0r");

		PO_LoginView.logout(driver);

		//Comprobamos que volvemos a la pagina de identificacion
		String checkText = PO_HomeView.getP().getString("login.message", PO_Properties.getSPANISH());
		List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
		Assertions.assertEquals(checkText, result.get(0).getText());

		checkText = PO_HomeView.getP().getString("login.disconnected", PO_Properties.getSPANISH());
		result = PO_View.checkElementBy(driver, "text", checkText);
		Assertions.assertEquals(checkText, result.get(0).getText());

	}

	/**
    [Prueba6] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
     */
	@Test
	@Order(6)
	void Prueba6() {
		//Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

		//Rellenamos el formulario
		PO_LoginView.fillForm(driver, "12345678Z", "@Dm1n1str@D0r");

		//comprobamos que podemos cerrar sesion al estar autenticados
		String checkText = PO_HomeView.getP().getString("disconnect", PO_Properties.getSPANISH());
		List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
		Assertions.assertEquals(checkText, result.get(0).getText());

		PO_LoginView.logout(driver);

		//No se deberia ver "Cerrar Sesion"
		String textoNoPresente = checkText;

		// Buscamos elementos que contengan el texto
		List<WebElement> elementos = driver.findElements(By.xpath("//*[contains(text(), '" + textoNoPresente + "')]"));

		// Comprobamos que la lista esté vacía
		Assertions.assertTrue(elementos.isEmpty(), "El texto '" + textoNoPresente + "' está presente en la página.");
	}

	/**
	 [Prueba7] Registro de un Empleado con datos válidos.
	 */
	@Test
	@Order(7)
	void Prueba7() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		PO_PrivateView.registerUser(driver);
		PO_LoginView.logout(driver);
	}

	/**
	 [Prueba8] Registro de un usuario con datos inválidos (DNI vacío, nombre vacío, apellidos vacíos)
	 */
	@Test
	@Order(8)
	void Prueba8() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.empty", PO_Properties.getSPANISH());
		PO_PrivateView.registerUserError(driver, "", "", "", error);
		PO_LoginView.logout(driver);
	}

	/**
	 [Prueba9] Registro de un usuario con datos inválidos (DNI con formato incorrecto)
	 */
	@Test
	@Order(9)
	void Prueba9() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.register.dni.format", PO_Properties.getSPANISH());
		PO_PrivateView.registerUserError(driver, "1723098712", "pepep", "pepe", error);
		PO_LoginView.logout(driver);
	}

	/**
	 [Prueba10] Registro de Usuario con datos inválidos (DNI existente)
	 */
	@Test
	@Order(10)
	void Prueba10() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.register.dni.duplicate", PO_Properties.getSPANISH());
		PO_PrivateView.registerUserError(driver, "12345678Z", "pepe", "pepe", error);
		PO_LoginView.logout(driver);
	}

	/**
        [Prueba11] Registro de un Vehículos con datos válidos
     */
	@Test
	@Order(11)
	void Prueba11() {

		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		PO_PrivateView.registerVehicle(driver);
		PO_LoginView.logout(driver);
	}
	/**
    [Prueba12] Registro de un Vehículos con datos inválidos (matrícula vacía, número de bastidor vacío,
        marca y modelo vacíos.
     */
	@Test
	@Order(12)
	void Prueba12() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.empty", PO_Properties.getSPANISH());
		PO_PrivateView.registerVehicleError(driver, "", "", "","", error);
		PO_LoginView.logout(driver);
	}
	/**
    [Prueba13] Registro de un Vehículos con datos inválidos (formato de matrícula inválido).
     */
	@Test
	@Order(13)
	void Prueba13() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.vehicle.plate.format", PO_Properties.getSPANISH());
		PO_PrivateView.registerVehicleError(driver, "1341341341341341", "11111111111111111", "toyota","qwe" ,error);
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba14] Registro de un Vehículos con datos inválidos (longitud del número de bastidor inválido).
     */
	@Test
	@Order(14)
	void Prueba14() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.vehicle.chassisNumber.length", PO_Properties.getSPANISH());
		PO_PrivateView.registerVehicleError(driver, "1234ABC", "1", "toyota","qwe" ,error);
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba15] Registro de un Vehículos con datos inválidos (matrícula existente)

     */
	@Test
	@Order(15)
	void Prueba15() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.vehicle.plate.duplicate", PO_Properties.getSPANISH());
		PO_PrivateView.registerVehicleError(driver, "1234BCD", "11111111111111111", "toyota","qwe" ,error);
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba16] Registro de un Vehículos con datos inválidos (número de bastidor existente).
     */
	@Test
	@Order(16)
	void Prueba16() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error = PO_HomeView.getP().getString("Error.vehicle.chassisNumber.repeated", PO_Properties.getSPANISH());
		PO_PrivateView.registerVehicleError(driver, "1234ABC", "12345678901234111", "toyota","qwe" ,error);
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba17]
	 * Mostrar el listado de empleados y comprobar que se muestran todos los que existen en el sistema, incluyendo el
	 * empleado actual y los empleados administradores
	 */
	@Test
	@Order(17)
	void Prueba17() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");

		//comprobamos que se muestran todos los que hay actualmente en el sistema
		PO_PrivateView.listUsers(driver, usersService.getUsers());

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba18]
	 * Autenticarse como administrador, editar un usuario estándar, cambiando su rol a administrador, DNI, nombre y
	 * apellidos, comprobar que los datos se han actualizados correctamente. Salir de sesión como administrador y
	 * autenticarse como el usuario modificado y acceder a la funcionalidad de listado de usuarios del sistema
	 * para probar el nuevo rol de administrador.
	 */
	@Test
	@Order(18)
	void Prueba18() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");

		//editamos el primer usuario
		String[] values = PO_PrivateView.editUser(driver, usersService.getStandardUsers());
		PO_LoginView.logout(driver);

		//entramos en sesión como el nuevo usuario
		PO_LoginView.login(driver, values[0], "123456");

		//Como Admin, deberiamos poder entrar a registar vehiculo
		driver.navigate().to(URL + "/user/list");
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba19]
	 * Editar un empleado introduciendo datos inválidos (DNI existente asignado a otro usuario del sistema, nombre y
	 * apellidos vacíos), comprobar que se devuelven los mensajes de error correctamente y que el empleado no se actualiza.
	 */
	@Test
	@Order(19)
	void Prueba19() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String error1 = PO_HomeView.getP().getString("Error.register.dni.duplicate", PO_Properties.getSPANISH());
		String error2 = PO_HomeView.getP().getString("Error.empty", PO_Properties.getSPANISH());
		PO_PrivateView.editUserError(driver, "99999992C", "12345678Z", "", "", error1, error2);
		PO_LoginView.logout(driver);
	}

	/**
	[Prueba20] Mostrar el listado de vehículos y comprobar que se muestran todos los que existen en el
sistema.
	 */
	@Test
	@Order(20)
	void Prueba20() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		PO_PrivateView.listVehicles(driver, vehicleService.getVehicles());
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba21] Ir a la lista de vehículos, borrar el primer vehículo de la lista, comprobar que la lista se actualiza
        y dicho vehículo desaparece.
     */
	@Test
	@Order(21)
	void Prueba21() {


		// Iniciar sesión como administrador
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");

		// Ir al listado de vehículos
		PO_PrivateView.goToVehicleList(driver);

		// Obtener la matrícula del primer vehículo
		WebElement primeraFila = driver.findElement(By.xpath("//table/tbody/tr[1]"));
		String matricula = primeraFila.findElement(By.xpath("td[2]")).getText(); // td[2] = matrícula

		// Seleccionar su checkbox
		WebElement checkbox = primeraFila.findElement(By.xpath("td[1]/input[@type='checkbox']"));
		checkbox.click();

		// Pulsar botón "Eliminar seleccionados"
		WebElement botonEliminar = driver.findElement(By.cssSelector("button[type='submit'].btn-danger"));
		botonEliminar.click();

		// Esperar a que recargue la lista

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Vehiculos')]", 3);

		// Verificar que la matrícula ya no aparece en la tabla
		List<WebElement> coincidencias = driver.findElements(By.xpath("//table/tbody/tr/td[contains(text(),'" + matricula + "')]"));
		Assertions.assertTrue(coincidencias.isEmpty(), "El vehículo con matrícula " + matricula + " sigue presente tras ser eliminado.");
		PO_LoginView.logout(driver);
	}


	/**
    [Prueba22] Ir a la lista de vehículos, borrar el último vehículo de la lista, comprobar que la lista se actualiza
        y dicho vehículo desaparece.
     */
	@Test
	@Order(22)
	void Prueba22() {

		// Iniciar sesión como administrador
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");

		// Ir al listado de vehículos
		PO_PrivateView.goToVehicleList(driver);

		// Obtener todas las filas de la tabla (vehículos)
		List<WebElement> filas = driver.findElements(By.xpath("//table/tbody/tr"));
		int ultimaPosicion = filas.size();

		// Obtener la matrícula del último vehículo
		WebElement ultimaFila = driver.findElement(By.xpath("//table/tbody/tr[" + ultimaPosicion + "]"));
		String matricula = ultimaFila.findElement(By.xpath("td[2]")).getText();

		// Seleccionar su checkbox
		WebElement checkbox = ultimaFila.findElement(By.xpath("td[1]/input[@type='checkbox']"));
		checkbox.click();

		// Pulsar botón "Eliminar seleccionados"
		WebElement botonEliminar = driver.findElement(By.cssSelector("button[type='submit'].btn-danger"));
		botonEliminar.click();

		// Esperar a que recargue la lista
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Vehiculos')]", 3);

		// Verificar que la matrícula ya no aparece en la tabla
		List<WebElement> coincidencias = driver.findElements(By.xpath("//table/tbody/tr/td[contains(text(),'" + matricula + "')]"));
		Assertions.assertTrue(coincidencias.isEmpty(), "El vehículo con matrícula " + matricula + " sigue presente tras ser eliminado.");
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba23] Ir a la lista de vehículos, borrar 3 vehículos, comprobar que la lista se actualiza y dichos
        vehículos desaparecen.
     */
	@Test
	@Order(23)
	void Prueba23() {

		// Iniciar sesión como administrador
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");

		// Ir al listado de vehículos
		PO_PrivateView.goToVehicleList(driver);

		// Obtener las filas actuales
		List<WebElement> filas = driver.findElements(By.xpath("//table/tbody/tr"));
		int totalFilas = filas.size();

		// Asegurarse de que haya al menos 3 vehículos
		Assertions.assertTrue(totalFilas >= 3, "No hay suficientes vehículos para realizar la prueba.");

		// Lista para almacenar las matrículas que vamos a eliminar
		List<String> matriculasEliminadas = new ArrayList<>();

		// Seleccionar los 3 primeros vehículos y guardar sus matrículas
		for (int i = 1; i <= 3; i++) {
			WebElement fila = driver.findElement(By.xpath("//table/tbody/tr[" + i + "]"));

			// Obtener matrícula (td[2])
			String matricula = fila.findElement(By.xpath("td[2]")).getText();
			matriculasEliminadas.add(matricula);

			// Seleccionar su checkbox (td[1]/input)
			WebElement checkbox = fila.findElement(By.xpath("td[1]/input[@type='checkbox']"));
			checkbox.click();
		}

		// Pulsar botón "Eliminar seleccionados"
		WebElement botonEliminar = driver.findElement(By.cssSelector("button[type='submit'].btn-danger"));
		botonEliminar.click();

		// Esperar a que recargue la lista
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Vehiculos')]", 3);

		// Verificar que ninguna de las matrículas eliminadas está en la nueva tabla
		for (String matricula : matriculasEliminadas) {
			List<WebElement> coincidencias = driver.findElements(By.xpath("//table/tbody/tr/td[contains(text(),'" + matricula + "')]"));
			Assertions.assertTrue(coincidencias.isEmpty(),
					"El vehículo con matrícula " + matricula + " sigue presente tras ser eliminado.");
		}
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba24]
      Mostrar el listado de trayectos y comprobar que se muestran todos los que tienen al empleado actual asignado como conductor.
     */
	@Test
	@Order(24)
	void Prueba24() {
		String userDNI = "99999992D";
		PO_LoginView.login(driver, userDNI, "123456");
		//Entramos en el elemento de trayectos ([1]) y luego en el ver trayectos ([2])
		driver.navigate().to(URL + "/path/list");

		// Obtener datos esperados de la base de datos
		List<Path> expectedPaths = pathService.getPathsByUserDni(userDNI); // Ajusta según tu lógica de negocio

		// Obtener los elementos de la lista desde la página web
		List<WebElement> pathListRows = driver.findElements(By.xpath("//table[@id='pathsTable']/tbody/tr"));

		// Verificar que el tamaño de la lista coincide
		assertEquals(15, pathListRows.size()*3, "El número de trayectos no coincide");

		//logout
		PO_LoginView.logout(driver);
	}

	/**
   [Prueba25] Inicio de trayecto válido.
    */
	@Test
	@Order(25)
	void Prueba25() throws Exception {

		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		//Esperar a que la nueva página cargue y verificar el mensaje de bienvenida
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


		WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'Bienvenidos a la pagina principal')]")
		));
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba26] Inicio de trayecto no válido (el empleado tiene otro trayecto en curso).
     */
	@Test
	@Order(26)
	void Prueba26() throws Exception {
		String userDNI = "99999992D";
		PO_LoginView.login(driver, userDNI, "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button2 = driver.findElement(By.xpath("//button[@type='submit']"));
		button2.click();

		//esperamos que nos muestre el mensaje de error

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(text(), 'El empleado ya tiene un trayecto activo')]")
		));
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba27] Inicio de trayecto no válido (el vehículo para el que se quiere iniciar el trayecto no está disponible).
     */
	@Test
	@Order(27)
	void Prueba27() throws Exception {

		String userDNI = "99999992E";
		PO_LoginView.login(driver, userDNI, "123456");

		PO_PrivateView.goToStartTrip(driver);

		//comprobar que NO se muestra ningun texto que contenga 1111ZZZ (vehiculo no disponible)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		boolean condicion = false;

		try {
			WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//*[contains(text(), '1111ZZZ')]")
			));
		} catch (Exception e) { //no deberia aparecer
			condicion = true;
		}

		Assertions.assertTrue(condicion);
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba28] Repostaje válido.
     */
	@Test
	@Order(28)
	void Prueba28() throws InterruptedException {
		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to(URL + "/refuel/new");
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement gasolinera = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("stationName")));

		PO_PrivateView.fillFormRegisterRefuel(driver,"GasProm", "1","10",5000);
		//nos redirige
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Vehiculos')]", 3));
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba29] Repostaje inválido (no hay un trayecto en curso).
     */
	@Test
	@Order(29)
	void Prueba29() throws InterruptedException {
		PO_LoginView.login(driver, "99999992D", "123456");

		//acabamos con el trayecto que ya tiene este usuario
		PO_PrivateView.goToStartTrip(driver);
		PO_PrivateView.goToEndTrip(driver, "5000");

		driver.navigate().to(URL + "/refuel/new");
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//h2[contains(text(),'Bienvenidos')]", 10));
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba30] Repostaje inválido (nombre de la estación vacío, precio vacío, cantidad vacía, odómetro vacío).
     */
	@Test
	@Order(30)
	void Prueba30() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to(URL + "/refuel/new");
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement gasolinera = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("stationName")));

		PO_PrivateView.fillFormRegisterRefuel(driver,"", "-1","-1",5000);

		//nos muestra mensajes de error
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[contains(text(),'El nombre de la estación es obligatorio')]", 3));
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[contains(text(),'El precio debe ser un número positivo')]", 3));
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[contains(text(),'La cantidad debe ser un número positivo')]", 3));

		PO_PrivateView.goToEndTrip(driver, "5005");
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba31] Repostaje inválido (precio y cantidad negativos).
     */
	@Test
	@Order(31)
	void Prueba31() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to(URL + "/refuel/new");
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement gasolinera = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("stationName")));

		PO_PrivateView.fillFormRegisterRefuel(driver,"GazProm", "-1","-1",5000);

		//nos muestra mensajes de error
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[contains(text(),'El precio debe ser un número positivo')]", 5));
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[contains(text(),'La cantidad debe ser un número positivo')]", 5));

		PO_PrivateView.goToEndTrip(driver, "5050");
		PO_LoginView.logout(driver);
	}

	/**
    [Prueba32] Repostaje inválido (odómetro anterior al del inicio del trayecto).
     */
	@Test
	@Order(32)
	void Prueba32() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to(URL + "/refuel/new");
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement gasolinera = wait2.until(ExpectedConditions.presenceOfElementLocated(By.name("stationName")));

		PO_PrivateView.fillFormRegisterRefuel(driver,"GazProm", "1","10",5);

		// cnos muestra mensajes de error
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'El odómetro debe ser mayor que el valor inicial del trayecto')]", 5));
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba33] Registro de fin de trayecto válido. Se asume que el usuario "userActive" tiene un trayecto activo.
	 * Se rellena el odómetro con un valor mayor al inicio del trayecto.
	 */
	@Test
	@Order(33)
	void Prueba33() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		PO_PrivateView.goToEndTrip(driver, "5060");

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba34] Registro de fin de trayecto inválido (odómetro vacío).Se asume que "userActive" tiene un trayecto activo,
	 * pero no se rellena el campo odómetro.
	 */
	@Test
	@Order(34)
	void Prueba34() throws InterruptedException {
		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		PO_PrivateView.goToEndTripError(driver, "0");

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba35] Registro de fin de trayecto inválido (odómetro negativo).Se asume que "userActive" tiene un trayecto activo,
	 * pero se rellena el campo con un valor negativo.
	 */
	@Test
	@Order(35)
	void Prueba35() throws InterruptedException {
		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		PO_PrivateView.goToEndTripError(driver, "-2");

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba36] Registro de fin de trayecto inválido (no hay trayectos en curso).Se asume que el usuario "userNoActive"
	 * no tiene un trayecto activo.
	 */
	@Test
	@Order(36)
	void Prueba36() throws InterruptedException {
		PO_LoginView.login(driver, "99999992D", "123456");

		PO_PrivateView.goToStartTrip(driver);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		PO_PrivateView.goToEndTrip(driver, "5090");

		PO_PrivateView.openDropdown(driver, "trayectosPersonales");

		// Espera a que aparezca el enlace correcto
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/path/end']", 5);
		WebElement link = driver.findElement(By.xpath("//a[@href='/path/end']"));
		link.click();

		//intentamos entrar en acabar un tryecto pero nos devuelven a la pagina pincipal
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Bienvenidos a la pagina principal')]", 5));

		PO_LoginView.logout(driver);
	}


	/**
	 * Prueba37] Mostrar el listado de trayectos y comprobar que se muestran todos los realizados por el
	 * vehículo con la matrícula seleccionada.
	 */
	@Test
	@Order(37)
	void Prueba37() throws InterruptedException {
		// Login
		PO_LoginView.login(driver, "99999992D", "123456");

		// Vamos a la pantalla principal de vehículos
		PO_PrivateView.openDropdown(driver, "trayectosPersonales");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[@href='/path/listFromCar']", 5);
		WebElement link = driver.findElement(By.xpath("//a[@href='/path/listFromCar']"));
		link.click();

		// Espera a que cargue el título de la página
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[contains(@class,'btn') and contains(text(),'Historial')]", 10);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement link2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//a[contains(@class,'btn') and contains(text(),'Historial')]"))
		);
		link2.click();


		// Ahora validamos que se ha cargado correctamente la tabla
		List<WebElement> pathListRows = driver.findElements(By.xpath("//table//tbody/tr"));

		// Comprobamos que hay filas (aquí puedes cambiar el número esperado)
		assertEquals(5, pathListRows.size(), "El número de trayectos no coincide");
	}

	/**
	 * [Prueba38]
	 * Mostrar el listado de repostajes y comprobar que se muestran todos los realizados por el vehículo con la matrícula seleccionada.
	 */
	@Test
	@Order(38)
	void Prueba38() {
		String userDNI = "99999990A";
		PO_LoginView.login(driver, userDNI, "123456");

		driver.get(URL + "/vehicle/free");

		// Obtener los elementos de la lista
		List<WebElement> vehicleListRows = driver.findElements(By.xpath("//table[@id='vehiclesTable']/tbody/tr"));

		String[] vehiculo;
		// Verificar contenido de cada trayecto
		for (int i = 0; i < vehicleListRows.size(); i++) {
			vehiculo = vehicleListRows.get(i).getText().split("\\s+");
			List<Refuel> expectedRefuels = refuelService.getRefulsByVehicle(vehiculo[0]);
			if (!expectedRefuels.isEmpty()) {
				driver.get(URL + "/vehicle/paths/" + expectedRefuels.get(0).getId());

				List<WebElement> refuelListRows = driver.findElements(By.xpath("//table[@id='refuelsTable']/tbody/tr"));

				String[] refuels;
				for (int j = 0; j < refuelListRows.size(); j++) {
					refuels = refuelListRows.get(j).getText().split("\\s+");
					assertEquals(refuels[0], expectedRefuels.get(j).getOnlyDate(), "No coincide la fecha: " + j);
					assertEquals(refuels[1], expectedRefuels.get(j).getOnlyTime(), "No coincide la hora: " + j);
					assertEquals(vehiculo[0], expectedRefuels.get(j).getVehicleRegistration(), "No coincide la matricula: " + i);
				}
			}
		}
	}

	/**
	 * [Prueba39]
	 * Mostrar el listado de vehículos disponibles y comprobar que se muestren sólo los que no estén siendo utilizados.
	 */
	@Test
	@Order(39)
	void Prueba39() {
		String userDNI = "99999990A";
		PO_LoginView.login(driver, userDNI, "123456");
		driver.get(URL + "/vehicle/free");

		List<WebElement> elementos = driver.findElements(By.xpath("//*[contains(text(), 'OCUPADO')]"));

		assertTrue(elementos.isEmpty());
	}

	/**
	 * [Prueba40]
	 * Modificación de contraseña con datos válidos.
	 */
	@Test
	@Order(40)
	void Prueba40() {
		PO_LoginView.login(driver, "12345678Z", "@Dm1n1str@D0r");
		String newPassword = "a123456789A-";
		PO_PrivateView.changePassword(driver, "@Dm1n1str@D0r", newPassword);
		PO_LoginView.logout(driver);
		PO_LoginView.login(driver, "12345678Z", newPassword);
		PO_PrivateView.listUsers(driver, usersService.getUsers());

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba41]
	 * Modificación de contraseña con datos inválidos (contraseña anterior incorrecta).
	 */
	@Test
	@Order(41)
	void Prueba41() {
		PO_LoginView.login(driver, "12345678Z", "a123456789A-");
		String error = PO_HomeView.getP().getString("Error.password.change.incorrect", PO_Properties.getSPANISH());
		PO_PrivateView.changePasswordError(driver, "@Dm1n1str@D0r", "a123456789A-", "a123456789A-", error);
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba42]
	 * Modificación de contraseña con datos inválidos (nueva contraseña débil).
	 */
	@Test
	@Order(42)
	void Prueba42() {
		PO_LoginView.login(driver, "12345678Z", "a123456789A-");
		String error = PO_HomeView.getP().getString("Error.password.change.weak", PO_Properties.getSPANISH());
		PO_PrivateView.changePasswordError(driver, "a123456789A-", "123456", "123456", error);
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba43]
	 * Modificación de contraseña con datos inválidos (repetición de contraseña inválida).
	 */
	@Test
	@Order(43)
	void Prueba43() {
		PO_LoginView.login(driver, "12345678Z", "a123456789A-");
		String error = PO_HomeView.getP().getString("Error.password.change.different", PO_Properties.getSPANISH());
		PO_PrivateView.changePasswordError(driver, "a123456789A-", "123456789aA-", "123456789aA#", error);
		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba44]
	 * Visualizar al menos tres páginas en español/inglés/español (comprobando que algunas de las etiquetas cambian al idioma correspondiente).
	 * Ejemplo, Página principal/Opciones Principales de Empleado/Listado de Empleados.
	 */
	@Test
	@Order(44)
	void Prueba44() {
		PO_LoginView.login(driver, "66778899A", "123456");

		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'usuarios')]", 5));

		//cambiamos idioma
		PO_PrivateView.openDropdown(driver, "btnLanguage");
		// Esperar a que el elemento sea visible
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnEnglish']", 5);
		//clic con JavaScript
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('btnEnglish').click();");


		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Language')]", 5));

		driver.navigate().to(URL + "/user/list");

		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'users')]", 5));

		//cambiamos idioma
		PO_PrivateView.openDropdown(driver, "btnLanguage");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnSpanish']", 5);
		//clic con JavaScript
		JavascriptExecutor js2 = (JavascriptExecutor) driver;
		js2.executeScript("document.getElementById('btnSpanish').click();");


		//se ve en español
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Usuarios')]", 5));

		driver.navigate().to(URL + "/vehicle/list");

		//se ve en español
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Vehiculos')]", 5));

		//cambiamos ingles
		PO_PrivateView.openDropdown(driver, "btnLanguage");
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnEnglish']", 5);
		//clic con JavaScript
		js.executeScript("document.getElementById('btnEnglish').click();");


		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'vehicles')]", 5));

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba45]
	 * Visualizar al menos tres páginas en inglés/francés o idioma elegido/inglés (comprobando que algunas de las etiquetas cambian al idioma correspondiente).
	 * Ejemplo, Página principal/Opciones Principales de Empleado/Listado de Empleados.
	 */
	@Test
	@Order(45)
	void Prueba45() {
		PO_LoginView.login(driver, "66778899A", "123456");

		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'usuarios')]", 5));

		//cambiamos idioma
		PO_PrivateView.openDropdown(driver, "btnLanguage");
		// Esperar a que el elemento sea visible
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnEnglish']", 5);
		//clic con JavaScript
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('btnEnglish').click();");


		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Language')]", 5));

		driver.navigate().to(URL + "/user/list");

		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'users')]", 5));

		//cambiamos idioma a frances
		PO_PrivateView.openDropdown(driver, "btnLanguage");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnFrench']", 5);
		//clic con JavaScript
		JavascriptExecutor js2 = (JavascriptExecutor) driver;
		js2.executeScript("document.getElementById('btnFrench').click();");


		//se ve en frances
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'utilisateurs')]", 5));

		driver.navigate().to(URL + "/vehicle/list");

		//se ve en frances
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'vehicules')]", 5));

		//cambiamos ingles
		PO_PrivateView.openDropdown(driver, "btnLanguage");
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@id='btnEnglish']", 5);
		//clic con JavaScript
		js.executeScript("document.getElementById('btnEnglish').click();");


		//se ve en ingles
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'vehicles')]", 5));

		PO_LoginView.logout(driver);
	}


	/**
	 * [Prueba46]
	 * Intentar acceder sin estar autenticado a la opción de listado de empleados. Se deberá volver al formulario de login.
	 */
	@Test
	@Order(46)
	void Prueba46() {
		driver.navigate().to(URL + "/user/list");
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Identificate')]", 5));
	}

	/**
	 * [Prueba47]
	 * Intentar acceder sin estar autenticado a la opción de listado de vehículos disponibles. Se deberá volver al formulario de login.
	 */
	@Test
	@Order(47)
	void Prueba47() {
		driver.navigate().to(URL + "/vehicle/list");
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Identificate')]", 5));
	}

	/**
	 * [Prueba48]
	 * Estando autenticado como usuario estándar intentar acceder a una opción disponible solo para usuarios administradores
	 * (Añadir menú de auditoria (visualizar logs)). Se deberá indicar un mensaje de acción prohibida.
	 */
	@Test
	@Order(48)
	void Prueba48() {
		driver.navigate().to(URL + "/user/logs");
		Assertions.assertNotNull(SeleniumUtils.waitLoadElementsBy(driver, "free",
				"//*[contains(text(),'Identificate')]", 5));
	}

	/**
	 * [Prueba49]
	 * Estando autenticado como usuario administrador visualizar todos los logs generados en una serie de interacciones.
	 * Esta prueba deberá generar al menos dos interacciones de cada tipo y comprobar que el listado incluye los logs correspondientes.
	 */
	@Test
	@Order(49)
	void Prueba49() {
		// 2 login fallidos,
		PO_LoginView.login(driver, "66666666Q", "Dm1n1str@D0r"); //LOGIN-ERR
		PO_LoginView.login(driver, "66666666Q", "@Dm0n1str@D0r");//LOGIN-ERR

		PO_LoginView.login(driver, "66666666Q", "123456"); //LOGIN-EX

		// 2 vehiculos registrados
		PO_PrivateView.registerNewVehicle(driver, "7654JRB", "92837465748392098"); //ALTA
		PO_PrivateView.registerNewVehicle(driver, "0650JRB", "02830465748392098"); //ALTA

		// logout y volver a hacer login
		PO_LoginView.logout(driver); //LOGOUT
		PO_LoginView.login(driver, "66666666Q", "123456"); //LOGIN-EX

		driver.navigate().to(URL + "/logs");

		// Esperar a que la tabla esté visible
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logsTable")));

		// Obtener las filas de la tabla
		WebElement table = driver.findElement(By.id("logsTable"));

		// Encuentra todas las filas dentro del tbody
		List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

		// Verifica que haya exactamente 5 filas
		Assertions.assertEquals(true, rows.size() > 9);

		PO_LoginView.logout(driver);
	}

	/**
	 * [Prueba50]
	 * Estando autenticado como usuario administrador, ir a visualización de logs y filtrar por un tipo,
	 * pulsar el botón/enlace borrar logs y comprobar que se eliminan los logs del tipo seleccionado, de la base de datos.
	 */
	@Test
	@Order(50)
	void Prueba50() {
		PO_LoginView.login(driver, "66666666Q", "123456"); // LOGIN-EX

		driver.navigate().to(URL + "/logs");


		// Hacer clic en el botón de borrar logs
		WebElement deleteLogsButton = driver.findElement(By.cssSelector("button.btn-danger"));
		deleteLogsButton.click();

		// Esperar a que la tabla esté visible
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

		// Obtener las filas de la tabla
		WebElement table = driver.findElement(By.id("logsTable"));

		// Encuentra todas las filas dentro del tbody
		List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

		Assertions.assertEquals(true, rows.size() <= 2 );


	}

	/**
	 * [Prueba51] Acceder al sistema para crear incidencias y registrar una incidencia sin respuesta. La incidencia
	 * 	debe registrarse correctamente y mostrarse en la vista de listado de incidencias.
	 */
	@Test
	@Order(51)
	void Prueba51() {
		PO_LoginView.login(driver, "99999992D", "123456"); // LOGIN-EX

		PO_PrivateView.goToStartTrip(driver);
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to("http://localhost:8100/incidencia/add");

		// false --> sin respuesta
		PO_PrivateView.fillFormCreateIncidencia(driver, "abcd", "ssdfsdf", "", false);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/incidencia/list");
	}


	/**
	 * [Prueba52] Acceder al sistema para crear incidencias y registrar una incidencia con respuesta. La incidencia
	 * 	debe registrarse correctamente y mostrarse en la vista de listado de incidencias.
	 */
	@Test
	@Order(52)
	void Prueba52() {
		PO_LoginView.login(driver, "99999992D", "123456"); // LOGIN-EX

		PO_PrivateView.goToStartTrip(driver);
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to("http://localhost:8100/incidencia/add");

		// true --> con respuesta
		PO_PrivateView.fillFormCreateIncidencia(driver, "abcd", "ssdfsdf", "", true);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/incidencia/list");
	}

	/**
	 *[Prueba53] Entrar en el listado de incidencias del sistema y comprobar que se muestran todas en el orden
	 * indicado.
	 */
	@Test
	@Order(53)
	void Prueba53() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456"); // LOGIN-EX

		PO_PrivateView.goToStartTrip(driver);
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to("http://localhost:8100/incidencia/add");

		// true --> con respuesta
		PO_PrivateView.fillFormCreateIncidencia(driver, "abcd", "ssdfsdf", "descripcion", true);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/incidencia/list");

		PO_LoginView.logout(driver);

		PO_LoginView.login(driver, "66666666Q", "123456"); // LOGIN-EX

		driver.navigate().to("http://localhost:8100/incidencia/listAdmin");

		//titulo de la incidencia anterior
		String textoBuscado = "abcd";
		boolean textoPresente = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado + "')]")).size() > 0;

		assertTrue(textoPresente);

	}




	/**
	 * [Prueba54] Acceder al detalle de una incidencia con estado “REGISTRADA”. Se debe modificar de
	 * 	forma automática el estado a “LEIDA”.
	 */
	@Test
	@Order(54)
	void Prueba54() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456"); // LOGIN-EX

		driver.navigate().to("http://localhost:8100/incidencia/listAdmin");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[contains(text(),'Modificar')]", 10);
		WebElement link2 = driver.findElement(By.xpath("//a[contains(text(),'Modificar')]"));
		link2.click();

		String textoBuscado = "LEIDA";
		boolean textoPresente = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado + "')]")).size() > 0;

		assertTrue(textoPresente);
	}


	/**
	 * [Prueba55] Utilizar los botones de estado dentro del detalle de una incidencia para cambiar su estado a
	 * 	“EN PROCESO”.
	 */
	@Test
	@Order(55)
	void Prueba55() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456"); // LOGIN-EX

		driver.navigate().to("http://localhost:8100/incidencia/listAdmin");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[contains(text(),'Modificar')]", 10);
		WebElement link2 = driver.findElement(By.xpath("//a[contains(text(),'Modificar')]"));
		link2.click();

		String textoBuscado = "LEIDA";
		boolean textoPresente = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado + "')]")).size() > 0;
		assertTrue(textoPresente);

		//PROCESAMOS LA incidencia
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[contains(text(),'En Proceso')]", 10);
		WebElement link3 = driver.findElement(By.xpath("//button[contains(text(),'En Proceso')]"));
		link3.click();


		//vemos que ha cambiado su estado
		String textoBuscado2 = "EN PROCESO";
		boolean textoPresente2 = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado2 + "')]")).size() > 0;
		assertTrue(textoPresente2);
	}


	/**
	 * [Prueba56] Utilizar los botones de estado dentro del detalle de una incidencia para cambiar su estado a
	 * 	“TERMINADA”.
	 */
	@Test
	@Order(56)
	void Prueba56() throws InterruptedException {

		PO_LoginView.login(driver, "99999992D", "123456"); // LOGIN-EX

		PO_PrivateView.goToStartTrip(driver);
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[@type='submit']", 5);
		WebElement button = driver.findElement(By.xpath("//button[@type='submit']"));
		button.click();

		driver.navigate().to("http://localhost:8100/incidencia/add");

		// true --> con respuesta
		PO_PrivateView.fillFormCreateIncidencia(driver, "abcd", "ssdfsdf", "", true);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/incidencia/list");

		PO_LoginView.logout(driver);

		PO_LoginView.login(driver, "66666666Q", "123456"); // LOGIN-EX

		driver.navigate().to("http://localhost:8100/incidencia/listAdmin");

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//a[contains(text(),'Modificar')]", 10);
		WebElement link = driver.findElement(By.xpath("//a[contains(text(),'Modificar')]"));
		link.click();

		//PROCESAMOS LA incidencia
		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[contains(text(),'En Proceso')]", 10);
		WebElement link2 = driver.findElement(By.xpath("//button[contains(text(),'En Proceso')]"));
		link2.click();

		//vemos que ha cambiado su estado
		String textoBuscado2 = "EN PROCESO";
		boolean textoPresente2 = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado2 + "')]")).size() > 0;
		assertTrue(textoPresente2);

		SeleniumUtils.waitLoadElementsBy(driver, "free", "//button[contains(text(),'Terminada')]", 10);
		WebElement link3 = driver.findElement(By.xpath("//button[contains(text(),'Terminada')]"));
		link3.click();

		//vemos que ha cambiado su estado
		String textoBuscado3 = "TERMINADA";
		boolean textoPresente3 = driver.findElements(By.xpath("//*[contains(text(),'" + textoBuscado3 + "')]")).size() > 0;
		assertTrue(textoPresente3);
	}

	/**
	 * [Prueba57] Editar el trayecto con valores correctos
	 */
	@Test
	@Order(57)
	void Prueba57() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456");

		//vamos al formulario de edicion de un vehiculo
		driver.navigate().to("http://localhost:8100/path/edit/32");

		PO_PrivateView.fillFormEditPath(driver,"200", "2022","2222", false);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/path/listAdmin");
	}

	/**
	 * [Prueba58] Editar el trayecto con valores incorrectos (fecha de inicio posterior al fin)
	 */
	@Test
	@Order(58)
	void Prueba58() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456");

		//vamos al formulario de edicion de un vehiculo
		driver.navigate().to("http://localhost:8100/path/edit/32");

		PO_PrivateView.fillFormEditPath(driver,"200", "2022","2222", true);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/path/edit/32");
	}

	/**
	 * [Prueba59] Editar el trayecto con valores incorrectos (odómetro de inicio superior al de fin)
	 */
	@Test
	@Order(59)
	void Prueba59() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456");

		//vamos al formulario de edicion de un vehiculo
		driver.navigate().to("http://localhost:8100/path/edit/32");

		PO_PrivateView.fillFormEditPath(driver,"200", "2222","2022", false);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/path/edit/32");;
	}

	/**
	 * [Prueba60] Editar el trayecto con valores incorrectos (odómetros negativos)
	 */
	@Test
	@Order(60)
	void Prueba60() throws InterruptedException {

		PO_LoginView.login(driver, "66666666Q", "123456");

		//vamos al formulario de edicion de un vehiculo
		driver.navigate().to("http://localhost:8100/path/edit/32");

		PO_PrivateView.fillFormEditPath(driver,"200", "-1","-222", false);

		assertEquals(driver.getCurrentUrl(), "http://localhost:8100/path/edit/32");
	}



}
