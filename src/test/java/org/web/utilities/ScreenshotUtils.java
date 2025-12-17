package org.web.utilities;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    /**
     * Captures a full-page screenshot and saves it.
     * @param driver The WebDriver instance.
     * @param methodName The name of the failed test method.
     * @param browserName The browser the test ran on.
     */
    public static void captureScreenshot(WebDriver driver, String methodName, String browserName) {
        // 1. Create the target directory if it doesn't exist
        Path screenshotDir = Paths.get("target", "screenshots");
        try {
            Files.createDirectories(screenshotDir);
        } catch (IOException e) {
            System.err.println("Could not create screenshot directory: " + e.getMessage());
        }

        // 2. Define filename with timestamp and browser
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("%s_%s_%s.png", methodName, browserName, timestamp);
        File destinationFile = screenshotDir.resolve(filename).toFile();

        try {
            // 3. Take the screenshot
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // 4. Copy the file to the destination
            Files.copy(screenshotFile.toPath(), destinationFile.toPath());
            System.out.println("Screenshot captured successfully: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving screenshot: " + e.getMessage());
        } catch (Exception e) {
             System.err.println("Could not take screenshot. Driver might be null or closed: " + e.getMessage());
        }
    }
}
