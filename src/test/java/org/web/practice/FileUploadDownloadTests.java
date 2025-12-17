package org.web.practice;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.web.utilities.FileVerificationUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class FileUploadDownloadTests {

	private WebDriver driver;
	private Path tempDownloadDir;

	@BeforeClass
	public void setup() throws IOException {
		// Setup specific download folder for verification
		tempDownloadDir = FileVerificationUtils.createTempDownloadDir();

		// Configure Chrome to download to our temp dir automatically
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", tempDownloadDir.toAbsolutePath().toString());
		prefs.put("download.prompt_for_download", false);
		prefs.put("safebrowsing.enabled", true);
		options.setExperimentalOption("prefs", prefs);

		driver = new ChromeDriver(options);
	}

	/**
	 * (a) Standard Upload via <input type="file"> Also includes Grid/Remote
	 * configuration logic.
	 */
	@Test(enabled = true)
	public void testStandardFileUpload() {
		driver.get("https://the-internet.herokuapp.com/upload");

		/*
		 * KEY STEP FOR GRID/CLOUD: If running on Selenium Grid (SauceLabs,
		 * BrowserStack, local Grid), we must tell the driver to detect local files and
		 * transfer them to the remote node.
		 */
		/*
		 * if (driver instanceof RemoteWebDriver) { ((RemoteWebDriver)
		 * driver).setFileDetector(new LocalFileDetector()); }
		 */

		WebElement fileInput = driver.findElement(By.id("file-upload"));

		// Send absolute path to the input element
		String RELATIVE_TXT_PATH = "src/test/resources/test-text-upload";
		String absolutePath = Paths.get(RELATIVE_TXT_PATH).toAbsolutePath().toString(); // Platform independent file location
		fileInput.sendKeys(absolutePath);

		driver.findElement(By.id("file-submit")).click();

		// Verify
		WebElement header = driver.findElement(By.cssSelector("h3"));
		Assert.assertEquals(header.getText(), "File Uploaded!", "Header text mismatch");

		WebElement uploadedFiles = driver.findElement(By.id("uploaded-files"));
		System.out.println("\n" + uploadedFiles.getText());
		System.out.println("\n" + absolutePath);
		String expectedFileName = Paths.get(RELATIVE_TXT_PATH).getFileName().toString(); // Platform independent file location
		Assert.assertTrue(uploadedFiles.getText().contains(expectedFileName));
	}

	/**
	 * (b) Native Dialog Upload using Robot Class. WARNING: This is flaky, requires
	 * OS focus, and doesn't work in headless mode. Use only when <input> is hidden
	 * or obstructed.
	 */
	@Test(enabled = true)
	public void testNativeDialogUpload() throws AWTException, InterruptedException {
		// Navigate to web page and complete the from up until file input.
		driver.get("https://automationexercise.com/contact_us");
		driver.findElement(By.xpath("//input[@name='name'][@type='text']")).sendKeys("Doug Dimmadome");
		driver.findElement(By.xpath("//input[@name='email'][@type='email']")).sendKeys("dougdim1234@gmail.com");
		driver.findElement(By.xpath("//input[@name='subject'][@type='text']")).sendKeys("File Upload");
		driver.findElement(By.id("message")).sendKeys("This is an automation test for uploading a file.");

		// Copy file path to System Clipboard
		String RELATIVE_TXT_PATH = "src/test/resources/test-text-upload";
		String absolutePath = Paths.get(RELATIVE_TXT_PATH).toAbsolutePath().toString(); // Platform independent file location
		StringSelection selection = new StringSelection(absolutePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

		// Click an element that opens the Window/Mac file picker
		// Standard .click() often fails on <input type="file"> with InvalidArgumentException
		// Uses JavascriptExecutor to bypass this driver restriction
		WebElement uploadElement = driver.findElement(By.xpath("//input[@name='upload_file'][@type='file']"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", uploadElement);

		Thread.sleep(1000); // MUST wait for OS dialog to appear

		// Use Robot to paste and hit Enter
		Robot robot = new Robot();

		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);

		Thread.sleep(500); // Wait for paste

		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

		Thread.sleep(1000); // Wait for dialog to close

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		By submitBtn = By.xpath("//input[@type='submit']");
		wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();

		// Switch to browser alert and click OK
		Alert alert = driver.switchTo().alert();
		alert.accept();

		// Verify
		WebElement header = driver.findElement(By.cssSelector("div.status.alert-success"));
		Assert.assertEquals(header.getText(), "Success! Your details have been submitted successfully.");
	}

	/**
	 * (c) Verify Download by Size and Hash
	 */
	@Test(enabled = true)
	public void testFileDownloadVerification() throws IOException, NoSuchAlgorithmException {
		driver.get("https://the-internet.herokuapp.com/download");

		// For this demo, finds the first link that has a txt file and use that
		WebElement firstDownloadLink = driver.findElement(By.xpath("//a[contains(text(), '.txt')]"));
		String actualFileName = firstDownloadLink.getText();

		firstDownloadLink.click();

		// Wait for file to land in our custom temp dir
		File downloadedFile = FileVerificationUtils.waitForDownloadToComplete(tempDownloadDir, actualFileName, 10);

		Assert.assertNotNull(downloadedFile, "File was not downloaded within timeout");

		// Assert Size > 0
		long fileSize = downloadedFile.length();
		System.out.println("Downloaded file size: " + fileSize + " bytes");
		Assert.assertTrue(fileSize > 0, "File is empty");

		// Assert Integrity (Checksum)
		// In a real test, you would know the 'expected' hash beforehand.
		String calculatedHash = FileVerificationUtils.getFileChecksum(downloadedFile);
		System.out.println("SHA-256 Hash: " + calculatedHash);

		// Example assertion (Using a fake hash here, replace with real one for actual test)
		// Assert.assertEquals(calculatedHash, "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

		Assert.assertNotNull(calculatedHash, "Hash calculation failed");
	}

	@AfterClass
	public void teardown() throws IOException {
		if (driver != null) {
			driver.quit();
		}
		// Cleanup the temp directory
		FileVerificationUtils.cleanUpDirectory(tempDownloadDir);
	}
}