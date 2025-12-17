package org.web.practice;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.Test;

public class LoadingSpinnerDynamicWait {

	@Test(enabled = true)
	public void loadingSpinner() {
		WebDriver driver = null;
		try {
			System.out.println("\n*** Dynamic Wait: Test start! ***");

			driver = new EdgeDriver();

			// Platform independent file location
			String RELATIVE_HTML_PATH = "src/test/resources/LoadingSpinnerWebPage.html";
			String absolutePath = Paths.get(RELATIVE_HTML_PATH).toAbsolutePath().toString();

			// Convert the absolute path to a file URL format, required for driver.get()
			// Replace backslashes with forward slashes to ensure correct URL format
			String url = "file:///" + absolutePath.replace("\\", "/");
			driver.get(url);

			// WebElements to be tested
			WebElement loadingSpinner = driver.findElement(By.id("loader"));
			WebElement textBox = driver.findElement(By.id("myDiv"));

			// --- Step1: Verify that the loading spinner is visible and the text is hidden ---
			System.out.println("\n--- Initial State Check ---");

			// The loadingSpinner should be visible after page load
			assert loadingSpinner.isDisplayed() : "FAIL: The loader spinner should be visible immediately.";
			System.out.println("PASS: The loader spinner is visible.");

			// myDiv should be hidden
			assert !textBox.isDisplayed() : "FAIL: The textbox should be hidden initially.";
			System.out.println("PASS: The textbox is hidden.");

			// --- Step2: Verify that the loading spinner is hidden and the text is visible ---
			System.out.println("\n--- Waiting for 3 second delay using FluentWait ---");

			// Define fluentWait
			// Fully robust wait that covers scenarios where 'element' is found, but becomes stale before isDisplayed() is called
			Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(10)) // Maximum wait time of 10 seconds
					.pollingEvery(Duration.ofMillis(500)) // Polls the DOM every 500 milliseconds
					.ignoring(NoSuchElementException.class) // Ignore exception if element is missing/disappears during polling duration
					.ignoring(StaleElementReferenceException.class); // Ignore exception if element instance is no longer in the DOM (stale element)

			// Use the fluent wait until myDiv is visible
			WebElement finalTextBox = fluentWait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver d) {

					// Find the element again inside the wait function (until) to ensure freshness
					WebElement element = d.findElement(By.id("myDiv"));

					// Conditional to break out of the wait: element must be displayed
					if (element.isDisplayed()) {
						return element;
					}

					// If not displayed, null is returned and the wait continues to poll
					return null;
				}
			});

			System.out.println("PASS: The content in #myDiv became visible after the delay.");

			// After the delay, #loader must be hidden
			WebElement finalLoadingSpinner = driver.findElement(By.id("loader"));

			assert !finalLoadingSpinner.isDisplayed()
					: "FAIL: The loading spinner should be hidden after the content loads.";
			System.out.println("PASS: The loading spinner is hidden.");

			// After the delay, #myDiv is visible
			assert finalTextBox.isDisplayed() : "FAIL: The textbox should be visible after the content loads.";
			System.out.println("PASS: The textbox is displayed.");

			// Check the text inside of #myDiv
			WebElement h2Text = finalTextBox.findElement(By.tagName("h2"));
			WebElement pText = finalTextBox.findElement(By.tagName("p"));

			assert h2Text.getText().equals("Tada!")
					: "FAIL: Expected text 'Tada!' but found '" + h2Text.getText() + "' instead.";
			System.out.println("PASS: <h2> tag contains 'Tada!' as expected.");

			assert pText.getText().equals("Some text in my newly loaded page..")
					: "FAIL: Expected text 'Some text in my newly loaded page..' but found '" + pText.getText() + "' instead.";
			System.out.println("PASS: <p> tag contains 'Some text in my newly loaded page..' as expected.");
			System.out.println("\n*** Dynamic Wait: Test completed successfully! ***");

		} catch (AssertionError e) {
			System.err.println("Test Failed: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("An unexpected error occurred during the test execution: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}

	}
}
