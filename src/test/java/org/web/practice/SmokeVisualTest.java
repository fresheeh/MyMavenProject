package org.web.practice;

import org.web.utilities.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;

import org.openqa.selenium.By;


public class SmokeVisualTest {

    private WebDriver driver;
    private String browserName; // To store the browser name for the screenshot utility
    private WebDriverWait wait;
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By signinBtn = By.id("log-in");
    private By logoLabel = By.cssSelector("div.logo-label");
    private By elementHeader = By.cssSelector(".compact > h6.element-header");

    
    //Setup method to initialize the WebDriver and store browser name.
    @BeforeMethod
    @Parameters("browser")
    public void setup(String browser) {
        this.browserName = browser; // Store browser name
        System.out.println("Starting test on: " + browser);
        
        // Initialize WebDriver
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(new ChromeOptions());
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(new FirefoxOptions());
        } else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver(new EdgeOptions());
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Implicit wait
        driver.manage().window().maximize();
    }

     // Smoke Test Flow
    @Test(description = "Smoke Test and Key Assertions")
    public void smokeTestFlow() {
        String url = "https://demo.applitools.com/";
        driver.get(url);
        
        // Synchronization Discipline: Using Explicit Waits for stable element interaction
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        wait.until(ExpectedConditions.elementToBeClickable(signinBtn));
        
        driver.findElement(usernameInput).sendKeys("testuser");
        driver.findElement(passwordInput).sendKeys("password1234!");
        driver.findElement(signinBtn).click();
        
        // Assert that we are on the accounts overview page
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoLabel));
        wait.until(ExpectedConditions.visibilityOfElementLocated(elementHeader));

        String logoLabelText = driver.findElement(logoLabel).getText();
        String elementHeaderText = driver.findElement(elementHeader).getText();
        
        Assert.assertEquals(logoLabelText, "ACME", "Logo label check failed after login.");
        
        // Intentional Error Example: Change 'Financial Overview' to 'Accounts Overview' to force a failure
        Assert.assertEquals(elementHeaderText, "Accounts Overview", "Page element header check failed after login."); 
    }


    //Teardown method: Quits the driver and captures a screenshot if the test failed.
    @AfterMethod
    public void tearDown(ITestResult result) {
        // Capture screenshot ONLY if the test failed (Status.FAILURE)
        if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("Test failed. Capturing failure screenshot...");
            ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName(), this.browserName);
        }
        if (driver != null) {
            driver.quit();
        }
    }
    
}
