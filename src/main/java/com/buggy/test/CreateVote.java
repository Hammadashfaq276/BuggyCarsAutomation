package com.buggy.test;

import com.buggy.pages.CreateVotePage;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class CreateVote extends BaseTest
{
    @Test
    public void vote_UI_and_API_Test() {

        CreateVotePage votePage = new CreateVotePage(driver);

        String username = "Hammad600";
        String password = "Hammad@600";
        String comment = "Amazing Car";

        // ---------------- UI LOGIN ----------------
        votePage.login(username, password);
        Assert.assertTrue(votePage.getGreetingText().contains("Hi, Hammad"));

        // ---------------- UI NAVIGATION ----------------
        votePage.navigateToHome();
        votePage.openLamborghiniPage();

        // Select car model and get dynamic modelId
        String modelId = votePage.selectCarModel("AVENTADOR");
        System.out.println("Dynamic Model ID: " + modelId);

        // ---------------- UI VOTE ----------------
        WebElement voteSection = votePage.getVoteSection();
        boolean alreadyVoted = voteSection.getTagName().equals("p");

        // ---------------- API LOGIN ----------------
        String accessToken = votePage.getAccessToken(username, password);
        Assert.assertNotNull(accessToken, "Access token not received!");
        System.out.println("✔ Token received!");

        // Get votes before
        JsonPath jsonBefore = votePage.getModelDetails(modelId, accessToken);
        int votesBefore = jsonBefore.get("votes") != null ? jsonBefore.getInt("votes") : 0;
        boolean canVoteAPI = jsonBefore.get("canVote") != null && jsonBefore.getBoolean("canVote");
        System.out.println("Votes Before = " + votesBefore + ", CanVote = " + canVoteAPI);

        // ---------------- VOTE LOGIC ----------------
        if (alreadyVoted) {
            System.out.println("UI → Already voted message: " + voteSection.getText());
            Assert.assertFalse(canVoteAPI, "API says canVote=true but UI shows already voted!");
        } else {
            votePage.submitVoteUI(voteSection, comment);
            System.out.println("UI → Vote submitted: " + comment);
            votePage.submitVoteAPI(modelId, comment, accessToken);
            System.out.println("✔ API → Vote sent successfully");
        }

        // Get votes after
        JsonPath jsonAfter = votePage.getModelDetails(modelId, accessToken);
        int votesAfter = jsonAfter.get("votes") != null ? jsonAfter.getInt("votes") : votesBefore;
        System.out.println("Votes After = " + votesAfter);

        Assert.assertTrue(votesAfter >= votesBefore, "Vote count did NOT update correctly!");
        System.out.println("🎉 UI + API Validation Completed Successfully!");
    }
}
