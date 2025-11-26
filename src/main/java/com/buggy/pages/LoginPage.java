package com.buggy.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static io.restassured.RestAssured.given;

public class LoginPage {

    WebDriver driver;
    private static final String BASE_API_URL = "https://k51qryqov3.execute-api.ap-southeast-2.amazonaws.com/prod";

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ---------------- UI Locators ----------------
    @FindBy(name = "login")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(css = "button.btn.btn-success")
    private WebElement loginBtn;

    @FindBy(css = ".nav-link.disabled")
    private WebElement greetingLink;

    @FindBy(css = "li:nth-child(3) a:nth-child(1)")
    private WebElement nextLink;

    // ---------------- UI Actions ----------------
    public void enterUsername(String username) {
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordField.sendKeys(password);
    }

    public void clickLogin() {
        loginBtn.click();
    }

    public String getGreetingText() {
        return greetingLink.getText();
    }
    // Naya getter for WebElement
    public WebElement getGreetingLink() {
        return greetingLink;
    }
    public void clickNextLink() {
        nextLink.click();
    }

    // ---------------- API Methods ----------------
    public String getAccessToken(String username, String password) {

        RestAssured.baseURI = BASE_API_URL;

        Response tokenResponse = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/oauth/token")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return tokenResponse.jsonPath().getString("access_token");
    }

    public Response getCurrentUserDetails(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users/current")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

}
