package com.buggy.test;

import com.buggy.pages.RegisterPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.restassured.response.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.time.Duration;
import java.util.List;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

public class RegisterTest extends BaseTest {
    @Test
    public void registerUser_UI_and_API_Test() {

        RegisterPage registerPage = new RegisterPage(driver);

        // ------------------------
        // Test Data
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String username = "TestUser" + uniqueSuffix;
        String firstName = "Hammad";
        String lastName = "Ashfaq";
        String password = "Hammad@123";

        // ------------------------
        // UI Flow
        registerPage.openRegisterForm();
        registerPage.fillRegistrationForm(
                username, firstName, lastName, password
        );

        // ------------------------
        // API Flow
        String payload = registerPage.createRegisterPayload(
                username, firstName, lastName, password
        );

        Response response = registerPage.sendRegisterAPIRequest(payload);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Username Sent: " + username);
        System.out.println("API Status Code: " + statusCode);
        System.out.println("API Response Body: " + responseBody);

        // ------------------------
        // Verification
        Assert.assertEquals(statusCode, 201,
                "Expected 201 but got " + statusCode);

        System.out.println("✅ UI + API Combined POM Test Passed");
    }
}
