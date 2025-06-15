package com.uniovi.sdi2425entrega1ext514.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {

    /**
     * Rellena el formulario de login
     * @param driver
     * @param usernamep
     * @param passwordp
     */
    static public void fillForm(WebDriver driver, String usernamep, String passwordp) {

        WebElement dni = driver.findElement(By.name("username"));
        dni.click();
        dni.clear();
        dni.sendKeys(usernamep);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);

        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    /**
     * Realiza el proceso de login
     * @param driver
     * @param usernamep
     * @param passwordp
     */
    static public void login(WebDriver driver, String usernamep, String passwordp) {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, usernamep, passwordp);
    }

    /**
     * Realiza el logout
     * @param driver
     */
    static public void logout(WebDriver driver) {
        String loginText = PO_HomeView.getP().getString("login.message", PO_Properties.getSPANISH());
        PO_PrivateView.clickOption(driver, "logout", "text", loginText);
    }
}
