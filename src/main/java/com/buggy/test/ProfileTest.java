package com.buggy.test;

import com.buggy.pages.ProfilePage;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;

public class ProfileTest extends BaseTest {

    @Test
    public void profileUpdate_UI_and_API_Test() {

        ProfilePage profilePage = new ProfilePage(driver);

        String username = "Hammad341";
        String password = "Hammad@124";

        String gender = "Male";
        String age = "43";
        String address = "test address2";
        String phone = "03134332021";
        String hobby = "Reading";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // ---------------- UI LOGIN ----------------
        profilePage.login(username, password);
        wait.until(ExpectedConditions.visibilityOf(profilePage.getGreetingLink()));
        Assert.assertEquals(profilePage.getGreetingText(), "Hi, Hammad");

        // ---------------- UI PROFILE UPDATE ----------------
        profilePage.navigateToProfile();
        profilePage.enterGender(gender);
        profilePage.enterAge(age);
        profilePage.enterAddress(address);
        profilePage.enterPhone(phone);
        profilePage.selectHobby(hobby);
        profilePage.submitProfile();

        wait.until(ExpectedConditions.visibilityOf(profilePage.getSuccessMessageElement()));
        Assert.assertEquals(profilePage.getSuccessMessage(), "The profile has been saved successful");
        System.out.println("UI: Profile updated successfully!");

        // ---------------- API LOGIN ----------------
        String accessToken = profilePage.getAccessToken(username, password);
        Assert.assertNotNull(accessToken, "Token is null!");
        System.out.println("API Token = " + accessToken);

        // ---------------- API PROFILE UPDATE ----------------
        Response response = profilePage.updateProfileAPI(
                username, "Hammad", "Ashfaq", gender, age, address, phone, hobby, accessToken
        );

        Assert.assertEquals(response.getStatusCode(), 200);
        String responseBody = response.getBody().asString().trim();

        if (responseBody.equals("{}") || responseBody.isEmpty()) {
            System.out.println("API returned empty body. Status code 200 confirmed.");
        } else {
            System.out.println("API returned response: " + responseBody);
        }
    }
}
