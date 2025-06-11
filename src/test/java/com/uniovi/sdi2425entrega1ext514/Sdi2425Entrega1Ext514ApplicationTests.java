package com.uniovi.sdi2425entrega1ext514;

import com.uniovi.sdi2425entrega1ext514.pageobjects.PO_HomeView;
import com.uniovi.sdi2425entrega1ext514.pageobjects.PO_LoginView;
import com.uniovi.sdi2425entrega1ext514.pageobjects.PO_Properties;
import com.uniovi.sdi2425entrega1ext514.pageobjects.PO_View;
import com.uniovi.sdi2425entrega1ext514.services.PathService;
import com.uniovi.sdi2425entrega1ext514.services.RefuelService;
import com.uniovi.sdi2425entrega1ext514.services.UsersService;
import com.uniovi.sdi2425entrega1ext514.services.VehicleService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
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

}
