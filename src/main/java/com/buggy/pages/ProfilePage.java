package com.buggy.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.restassured.RestAssured.given;

public class ProfilePage {


    WebDriver driver;
    private static final String BASE_API_URL = "https://k51qryqov3.execute-api.ap-southeast-2.amazonaws.com/prod";

    public ProfilePage(WebDriver driver) {
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

    @FindBy(css = "a.nav-link[href='/profile']")
    private WebElement profileLink;

    @FindBy(id = "gender")
    private WebElement genderField;

    @FindBy(id = "age")
    private WebElement ageField;

    @FindBy(id = "address")
    private WebElement addressField;

    @FindBy(id = "phone")
    private WebElement phoneField;

    @FindBy(id = "hobby")
    private WebElement hobbyDropdown;

    @FindBy(css = "button[type='submit']")
    private WebElement submitBtn;

    @FindBy(css = "div.result.alert-success")
    private WebElement successMessage;

    // ---------------- UI Actions ----------------
    public void login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginBtn.click();
    }

    public WebElement getGreetingLink() {
        return greetingLink;
    }

    public String getGreetingText() {
        return greetingLink.getText().trim();
    }

    public void navigateToProfile() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(profileLink)).click();
    }

    public void enterGender(String gender) {
        WebElement field = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(genderField));
        field.clear();
        field.sendKeys(gender);
    }

    public void enterAge(String age) {
        WebElement field = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(ageField));
        field.clear();
        field.sendKeys(age);
    }

    public void enterAddress(String address) {
        WebElement field = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(addressField));
        field.clear();
        field.sendKeys(address);
    }

    public void enterPhone(String phone) {
        WebElement field = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(phoneField));
        field.clear();
        field.sendKeys(phone);
    }

    public void selectHobby(String hobby) {
        WebElement dropdown = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(hobbyDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(hobby);
    }

    public void submitProfile() {
        submitBtn.click();
    }

    public WebElement getSuccessMessageElement() {
        return successMessage;
    }

    public String getSuccessMessage() {
        return successMessage.getText().trim();
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

    public Response updateProfileAPI(String username, String firstName, String lastName,
                                     String gender, String age, String address, String phone, String hobby,
                                     String accessToken) {

        String profileBody = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"firstName\": \"" + firstName + "\",\n" +
                "  \"lastName\": \"" + lastName + "\",\n" +
                "  \"gender\": \"" + gender + "\",\n" +
                "  \"age\": \"" + age + "\",\n" +
                "  \"address\": \"" + address + "\",\n" +
                "  \"phone\": \"" + phone + "\",\n" +
                "  \"hobby\": \"" + hobby + "\",\n" +
                "  \"currentPassword\": \"\",\n" +
                "  \"newPassword\": \"\",\n" +
                "  \"newPasswordConfirmation\": \"\"\n" +
                "}";

        return given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(profileBody)
                .when()
                .put("/users/profile")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
