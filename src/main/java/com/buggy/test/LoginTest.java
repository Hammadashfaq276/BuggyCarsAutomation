package com.buggy.test;

import com.buggy.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.restassured.response.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;

public class LoginTest extends BaseTest {


    @Test
    public void login_UI_and_API_Test() {

        LoginPage loginPage = new LoginPage(driver);

        String username = "Hammad341";
        String password = "Hammad@124";

        // ---------------- UI Flow ----------------
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Wait for the greeting element to be visible
        wait.until(ExpectedConditions.visibilityOf(loginPage.getGreetingLink()));

        Assert.assertEquals(loginPage.getGreetingText(), "Hi, Hammad");

        loginPage.clickNextLink();

        // ---------------- API Flow ----------------
        String accessToken = loginPage.getAccessToken(username, password);
        Assert.assertNotNull(accessToken, "Access token is null!");
        System.out.println("Access Token: " + accessToken);

        Response userResponse = loginPage.getCurrentUserDetails(accessToken);

        String firstName = userResponse.jsonPath().getString("firstName");
        String lastName = userResponse.jsonPath().getString("lastName");
        boolean isAdmin = userResponse.jsonPath().getBoolean("isAdmin");

        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Is Admin: " + isAdmin);

        Assert.assertEquals(firstName, "Hammad");
        Assert.assertEquals(lastName, "Ashfaq");
        Assert.assertFalse(isAdmin, "User should not be admin");
    }


}
