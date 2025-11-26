package com.buggy.pages;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;

public class CreateVotePage {

    WebDriver driver;
    WebDriverWait wait;
    private static final String BASE_API_URL = "https://k51qryqov3.execute-api.ap-southeast-2.amazonaws.com/prod";

    public CreateVotePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

    @FindBy(css = ".navbar-brand")
    private WebElement homeLink;

    @FindBy(css = "img[title='Lamborghini']")
    private WebElement lamborghiniImg;

    @FindBy(css = ".btn.btn-success")
    private WebElement voteBtn;

    // ---------------- UI Actions ----------------
    public void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOf(usernameField)).sendKeys(username);
        passwordField.sendKeys(password);
        loginBtn.click();
        wait.until(ExpectedConditions.visibilityOf(greetingLink));
    }

    public String getGreetingText() {
        return greetingLink.getText().trim();
    }

    public void navigateToHome() {
        wait.until(ExpectedConditions.elementToBeClickable(homeLink)).click();
    }

    public void openLamborghiniPage() {
        wait.until(ExpectedConditions.elementToBeClickable(lamborghiniImg)).click();
    }

    public String selectCarModel(String modelName) {
        WebElement carLink = wait.until(ExpectedConditions.elementToBeClickable(
                org.openqa.selenium.By.xpath("//a[normalize-space()='" + modelName + "']")));
        String href = carLink.getAttribute("href");
        String modelId = href.substring(href.lastIndexOf("/") + 1);
        carLink.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.cssSelector("img.center-block")));
        return modelId;
    }

    public WebElement getVoteSection() {
        return wait.until(driver -> {
            List<WebElement> votedMsg = driver.findElements(org.openqa.selenium.By.cssSelector("div.card-block > p.card-text"));
            if (!votedMsg.isEmpty() && votedMsg.get(0).getText().contains("Thank you for your vote!")) {
                return votedMsg.get(0); // Already voted
            }
            List<WebElement> commentBox = driver.findElements(org.openqa.selenium.By.cssSelector("textarea.form-control"));
            if (!commentBox.isEmpty()) {
                return commentBox.get(0); // Can vote
            }
            return null;
        });
    }

    public void submitVoteUI(WebElement voteSection, String comment) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", voteSection);
        voteSection.sendKeys(comment);
        wait.until(ExpectedConditions.elementToBeClickable(voteBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.cssSelector("div.card-block > p.card-text")));
    }

    // ---------------- API Methods ----------------
    public String getAccessToken(String username, String password) {
        RestAssured.baseURI = BASE_API_URL;
        Response tokenRes = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .post("/oauth/token");
        return tokenRes.jsonPath().getString("access_token");
    }

    public JsonPath getModelDetails(String modelId, String accessToken) {
        Response res = given()
                .header("Authorization", "Bearer " + accessToken)
                .get("/models/" + modelId);
        return res.jsonPath();
    }

    public void submitVoteAPI(String modelId, String comment, String accessToken) {
        Response voteApiRes = given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body("{\"comment\":\"" + comment + "\"}")
                .post("/models/" + modelId + "/vote");
        if (voteApiRes.getStatusCode() != 200) {
            throw new RuntimeException("API vote failed with status: " + voteApiRes.getStatusCode());
        }
    }

}
