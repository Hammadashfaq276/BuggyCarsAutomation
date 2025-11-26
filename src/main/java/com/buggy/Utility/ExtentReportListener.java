package com.buggy.Utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.buggy.test.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportListener implements ITestListener {

    public static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>(); // for parallel safe

    WebDriver driver;

    @Override
    public void onStart(ITestContext context) {
        try {
            File reportDir = new File("ExtentReports");
            if (!reportDir.exists()) reportDir.mkdirs();

            String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            ExtentSparkReporter spark = new ExtentSparkReporter("ExtentReports/Report_" + date + ".html");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Add suite info / metadata
            extent.setSystemInfo("Tester", "Hammad Ashfaq");  // tester name
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", "Chrome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create a test in the same report
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest); // Thread safe for parallel execution
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        Object testClass = result.getInstance();
        driver = ((BaseTest) testClass).driver;

        String screenshotPath = takeScreenshot(result.getMethod().getMethodName(), driver);
        test.get().addScreenCaptureFromPath(screenshotPath);
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush(); // One report for all tests
    }

    public String takeScreenshot(String testName, WebDriver driver) {
        try {
            File screenshotDir = new File("Screenshots");
            if (!screenshotDir.exists()) screenshotDir.mkdirs();

            String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String path = "Screenshots/" + testName + "_" + date + ".png";
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(path);
            FileHandler.copy(src, dest);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}