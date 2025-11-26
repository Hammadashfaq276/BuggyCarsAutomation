package com.buggy.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage {

    WebDriver driver;
    private static final String BASE_API_URL =
            "https://k51qryqov3.execute-api.ap-southeast-2.amazonaws.com/prod";

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ---------------- UI Locators ----------------

    @FindBy(css = ".btn.btn-success-outline")
    private WebElement openRegisterBtn;

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "firstName")
    private WebElement firstNameField;

    @FindBy(id = "lastName")
    private WebElement lastNameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordField;

    // ---------------- UI Actions ----------------

    public void openRegisterForm() {
        openRegisterBtn.click();
    }

    public void fillRegistrationForm(String username,
                                     String firstName,
                                     String lastName,
                                     String password) {

        usernameField.sendKeys(username);
        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        passwordField.sendKeys(password);
        confirmPasswordField.sendKeys(password);
    }

    // ---------------- API Actions ----------------

    public String createRegisterPayload(String username,
                                        String firstName,
                                        String lastName,
                                        String password) {

        return "{"
                + "\"username\":\"" + username + "\","
                + "\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"" + lastName + "\","
                + "\"password\":\"" + password + "\","
                + "\"confirmPassword\":\"" + password + "\""
                + "}";
    }

    public Response sendRegisterAPIRequest(String payload) {

        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(payload)
                .post(BASE_API_URL + "/users");
    }

}
