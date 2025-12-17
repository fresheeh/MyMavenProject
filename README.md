# My Maven Project

### Overview
- Sandbox project to practice Selenium by exersicing automation testing concepts and use cases outline in the [Project Overview](#project-overview)
- More notes of particular concepts are written inside the comments of the relevant file(s).
- [Usage](#usage) section 

### Installation Prerequisites
- **Download the project:** Use one of the methods below
	- Clone repository using Git `git clone https://github.com/fresheeh/MyMavenProject.git` or GitHub Desktop
    - Download and extract project [zip](https://github.com/fresheeh/MyMavenProject/archive/refs/heads/main.zip)
- **Java Development Kit (JDK):** Version 17 or higher (Windows x64 Installer - https://download.oracle.com/java/17/archive/jdk-17.0.12_windows-x64_bin.exe).
- **Google Chrome Web Browser:** https://www.google.com/chrome/index.html
- **Microsoft Edge Web Browser:** https://www.microsoft.com/en-us/edge/download?form=MA13FJ
- **Firefox Web Browser:** https://www.firefox.com/en-US/thanks/

- **Maven Configuration Steps:** Ensure the `mvn` command is accessible from your system's command line. This requires setting the `MAVEN_HOME` environment variable and adding `%MAVEN_HOME%\bin` to your system's `Path` variable.


**1. Download and Extract Apache Maven**

**1.1 Download:** Download the latest stable **Binary zip archive** (e.g., apache-maven-3.9.x-bin.zip) from the official Apache Maven website (https://maven.apache.org/download.cgi).

**1.2 Extract:** Extract the contents of the zip file to a permanent location on your system. e.g., `C:\Program Files\Apache\apache-maven-3.9.11` (or similar).


**2. Open the Environment Variables Window**

**2.1:** Press the **Windows Key** on your keyboard.

**2.2:** Type `environment` and select **"Edit the system environment variables"** (this opens the System Properties window).

**2.3:** Click the **"Environment Variables..."** button at the bottom of the window.


**3. Set the** `MAVEN_HOME` **Variable:**
This tells Windows where the root of the Maven installation is.

**3.1:** In the **"User variables for [Your Name]"** section (the top box), click the **"New..."** button.

**3.2:** Enter the following details:
	
**3.2.1: Variable name:** `MAVEN_HOME`
	
**3.2.2: Variable value:** The absolute path to the folder where you extracted Maven (e.g., `C:\Program Files\Apache\apache-maven-3.9.11`).

**3.3:** Click **"OK"** to save the new variable.


**4. Update the System** `Path` **Variable**
This is the most critical step, as it tells the Command Prompt where to find the `mvn.cmd` file.

**4.1:** In the **"System variables"** section (the lower box), scroll down and find the variable named `Path`.

**4.2:** Select it and click the **"Edit..."** button.

**4.3:** In the Path editor window:

- **If the editor shows a list of paths:** Click **"New"** and enter the following path exactly:

```
%MAVEN_HOME%\bin
```

- **If you are experiencing difficulties:**

	- *To ensure stability and bypass variable resolution issues, you can instead use the full explicit path (e.g., `C:\Program Files\Apache\apache-maven-3.9.11\bin`), which is often the most reliable method*.

	- **If the editor shows a single string field:** Scroll to the end of the string, and add a semicolon followed by the path:

```
;%MAVEN_HOME%\bin
```

**4.4:** Click **"OK"** on all open windows (`Edit Path`, `Environment Variables`, and `System Properties`) to apply the changes.


**5. Verification**

**5.1:** Close any currently open Command Prompt or Terminal windows.

**5.2:** Open a **new, fresh** Command Prompt as Administrator.

**5.2.1:** Press the **Windows Key + R** on your keyboard.

**5.2.2:** Enter `cmd`

**5.2.3:** Press the **Ctrl + Shift + Enter** on your keyboard.

**5.3:** Run the following verification command:

```
mvn -v
```

**Expected Result:** If configured correctly, the output will display the Apache Maven version and your installed Java version, confirming the setup is complete.


### Usage
Runs the Maven build, executes all the tests, and opens the testing results report in a web browser.

**1. Open a Command Prompt as Administrator.**

**1.1:** Press the **Windows Key + R** on your keyboard.

**1.2:** Enter `cmd`

**1.3:** Press the **Ctrl + Shift + Enter** on your keyboard.

**2. Run the following command:** 
(replace `[project_folder_location]` with the path of the project e.g., `C:\Users\nfres\QA Workspace\MyMavenProject`)

```
cd [project_folder_location]\MyMavenProject
```

**3. Run the following command:**

```
mvn clean verify
```

### Contact info
Email: nfresh24@gmail.com

# Project Overview
1. [Dynamic UI: Loading spinner implementing a FluentWait](#1-dynamic-ui-loading-spinner-implementing-a-fluentwait)
2. [Locator Cookbook: Tricky element locators](#2-locator-cookbook-tricky-element-locators)
3. [File Handling: upload and download tests](#3-file-handling-upload-and-download-tests)
4. [API Automation](#4-api-automation)
5. [Data integrity & Data Driven Testing](#5-data-integrity--data-driven-testing)
6. [Cross-browser Consistency](#6-cross-browser-consistency)


### 1. Dynamic UI: Loading spinner implementing a FluentWait
- I created a HTML file [resources/LoadingSpinnerWebPage.html](src/test/resources/LoadingSpinnerWebPage.html) with a loading spinner that will disappear after 3 seconds, then a text box will show.
- [practice/LoadingSpinnerDynamicWait.java](src/test/java/org/web/practice/LoadingSpinnerDynamicWait.java) This test case verifies that the loading spinner is visible and the text box not displayed before 3 seconds. Then after the 3 seconds, verifies that the loading spinner is not displayed and the text box is visible and displays the expected text.
- To accomplish this, a `FluentWait` is implemented to provide more stability to the test case by a defining custom polling frequency and ignoring specific exceptions during the polling duration. 

First, the `FluentWait` is defined below:

```java
Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver)
		.withTimeout(Duration.ofSeconds(10))
		.pollingEvery(Duration.ofMillis(500))
		.ignoring(NoSuchElementException.class)
		.ignoring(StaleElementReferenceException.class);
```

- `withTimeout(Duration.ofSeconds(10))` 
	- Sets the maximum wait time of 10 seconds.
	- If the text box isn't visible within 10 seconds, the wait fails.
	
- `pollingEvery(Duration.ofMillis(500))` 
	- Polls the DOM every 500 milliseconds.
	
- `ignoring(NoSuchElementException.class)` 
	- Ignore the exception, `NoSuchElementException`, if element is missing/disappears during polling duration.

- `ignoring(StaleElementReferenceException.class)` 
	- Ignore exception, `StaleElementReferenceException`, if element instance is no longer in the DOM (stale element). 	
	- This ignore is added to cover scenarios where `element` is found but, becomes stale before `isDisplayed()` is called, which would throw a `StaleElementReferenceException` and fail the test. 
	
Next, `fluentWiat` is implemented to wait until the text box is visible:

```java
WebElement finalTextBox = fluentWait.until(new Function<WebDriver, WebElement>() {
	public WebElement apply(WebDriver d) {
		WebElement element = d.findElement(By.id("myDiv"));
		
		if(element.isDisplayed()) {
			return element;
		}
			return null;
	}
});
```

- `WebElement element = d.findElement(By.id("myDiv"));`
	- The text box element is found again inside of the wait function, `until()`, to ensure freshness.
- `if(element.isDisplayed()) { return element; }`
	- Conditional to break out of the wait function. `element` must be displayed. Conditional is checked every interval defined by `pollingEvery()`.
- `return null;`
	- If the text box is not displayed, `null` is returned and the wait continues to poll the DOM.
	

### 2. Locator Cookbook: Tricky element locators
- 5 examples of tricky element locators in [practice/TrickyLocators.java](src/test/java/org/web/practice/TrickyLocators.java)
- Explanation in [Documentation/LocatorCookbook.md](Documentation/LocatorCookbook.md)

### 3. File Handling: Upload and Download Tests
- [utilities/FileVerificationUtils.java](src/test/java/org/web/utilities/FileVerificationUtils.java)
	- Helper class for File System operations: Hashing, cleaning up temp folders, and verifying download completion.
- [practice/FileUploadDownloadTests.java](src/test/java/org/web/practice/FileUploadDownloadTests.java)
	- Test for uploading a file the standard way (best practice).
	- Test for uploading a file using Robot class (flaky method).
	- Test for verifying a downloaded file size and hash.


### 4. API Automation
- [practice/ApiTestSuite.java](src/test/java/org/web/practice/ApiTestSuite.java)
	- JSON Schema Definition.
	- Mock Auth Token Flow.
	- Positive & Negative response tests (includes JSON schema validation).
	- Mock Idempotent POST Scenario.


### 5. Data Integrity & Data Driven Testing
- Registration flow implementing TestNG, Selenium, Mock database layer, Object Repository, and Page Object Model (POM).
- [resources/registration-data.json](src/test/resources/registration-data.json): **The Data Model & JSON Data**
	- Defines what the data looks like.
	- Supplies the data to `@DataProvider`.
- [utilities/DatabaseUtil.java](src/test/java/org/web/utilities/DatabaseUtil.java): **Database Utility (The "Source of Truth")**
	- Implements a `MockDatabaseService`. In a real scenario, you would replace the `HashMap` logic with JDBC or Hibernate calls.
- [dataproviders/UserDataProvider.java](src/test/java/org/web/dataproviders/UserDataProvider.java): **The Data Provider & Fixture Factory**
	- Defines `@DataProvider` method.
	- This class reads the JSON and transforms it. 
	- Crucially, it uses a Fixture Factory approach to ensure uniqueness (appending timestamps/UUIDs) so tests never fail due to duplicate data.
- [resources/element.properties](src/test/resources/element.properties): **Object Repository**
	- Defines all `By` element locators implemented in the POM by using key/value pairs.
	- The major advantage of using object repository is the segregation of objects from test cases. If the locator value of one `WebElement` changes, only the object repository needs to be changed rather than making changes in all test cases in which the locator has been used. Maintaining an object repository increases the modularity of framework implementation.
- [pages/AutomationExerciseSignupPage.java](src/test/java/org/web/pages/AutomationExerciseSignupPage.java): **Page Object Model Class**
	- Page Object Model (POM) is a design pattern that creates an object repository for web UI elements. Each web page is represented by a corresponding class containing page-specific business logic and methods that perform actions.
	- POM separates test code from page-specific code, significantly improving maintainability and reducing duplication in automation frameworks.
	- In this example, `AutomationExerciseSignupPage` is the POM for the entire registration flow for demonstration purposes.
- [practice/RegistrationTests.java](src/test/java/org/web/practice/RegistrationTests.java): **The Test Class (Putting it together)**
	- This is where the logic flows: **Generate Unique Data → DB Seed (if needed) → UI Action → DB Verify → UI Verify → Rollback.**
	

### 6. Cross-browser Consistency
- [utilities/ScreenshotUtils.java](src/test/java/org/web/utilities/ScreenshotUtils.java)
	- Captures a full-page screenshot and saves it.
	- Prints screenshot location in the console.
- [practice/SmokeVisualTest.java](src/test/java/org/web/practice/SmokeVisualTest.java)
	- If a test fails, the `@AfterMethod` automatically captures a PNG file in the `target/screenshots` directory, providing the state of the UI at the point of failure.
	- Ensures cross-browser execution via `testng.xml` configuration and `@Parameter()` test method annotation.
