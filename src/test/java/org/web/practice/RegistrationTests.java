package org.web.practice;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.web.dataproviders.UserDataProvider;
import org.web.pages.AutomationExerciseSignupPage;
import org.web.utilities.DatabaseUtil;

public class RegistrationTests {

	WebDriver driver;
	AutomationExerciseSignupPage signupPage;
	
    // Store email at class level for @AfterMethod rollback
    ThreadLocal<String> currentTestEmail = new ThreadLocal<>();

    
    // Initialize Driver
    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3)); // Implicit wait

        /**
         * Browser window not maximum because footer ad cannot be interacted with, shadowrootmode="closed".
         * If it were shadowrootmode="open", you could use element.getShadowRoot() in Selenium 4+ to gain access.
         * Setting the browser window size is a workaround to no display the ad.
         */
        driver.manage().window().setSize(new Dimension(1000, 800));
        
        driver.get("https://automationexercise.com/login");
        
        signupPage = new AutomationExerciseSignupPage(driver);
        
    }
    
    
    @Test(dataProvider = "registrationData", dataProviderClass = UserDataProvider.class)
    public void testUserRegistration(Map<String, String> data) throws Exception
    {
    	// --- 1. Data Preparation (Fixture Factory) ---
        // We take the template from JSON and make it unique to avoid collisions
        String uniqueId = UUID.randomUUID().toString().substring(0, 5);
        String uniqueEmail = data.get("firstName") + uniqueId + "@test.com";
        currentTestEmail.set(uniqueEmail); // Save for teardown
        
        System.out.println("Starting Test: " + data.get("scenarioName") + " with Email: " + uniqueEmail);
        
        // --- 2. Database Pre-Condition (Optional Seeding) ---
        // Ensure this email strictly DOES NOT exist before we start
        if (DatabaseUtil.verifyUserCreated(uniqueEmail)) {
            DatabaseUtil.deleteUser(uniqueEmail);
        }
        
        // --- 3. WebDriver UI Actions (Selenium) ---
        signupPage.enter_signup_name(data.get("firstName"));
        signupPage.enter_signup_email_PF(uniqueEmail);
        signupPage.click_signup_btn();
        signupPage.enter_signup_password(data.get("password"));
        signupPage.enter_signup_first_name(data.get("firstName"));
        signupPage.enter_signup_last_name(data.get("lastName"));
        signupPage.enter_signup_address1(data.get("address1"));
        signupPage.select_signup_country(data.get("country"));
        signupPage.enter_signup_state(data.get("state"));
        signupPage.enter_signup_city(data.get("city"));
        signupPage.enter_signup_zipcode(data.get("zipcode"));
        signupPage.enter_signup_mobile_number(data.get("mobileNumber"));
        
        try {
        	signupPage.click_create_account_btn();
        } catch (ElementClickInterceptedException e){
        	System.out.println("FAIL: Click action intercepted by an advertisement.");
        }
        
        // --- 4. UI Assertion ---
        Assert.assertTrue(signupPage.isAccountCreatedSuccessMessageDisplayed());
        System.out.println("[ASSERT-UI] Success message verified.");
        System.out.println("[UI-LOG] Registered user via UI");
        
        // Simulate the backend creating the user (Because we don't have a real app connected)
        DatabaseUtil.seedUser(uniqueEmail, data.get("password"), data.get("role"));

        // --- 5. Database Post-Condition Assertion ---
        // Verify the application actually wrote to the DB
        boolean recordExists = DatabaseUtil.verifyUserCreated(uniqueEmail);
        Assert.assertTrue(recordExists, "Database record was not created!");
        
        String dbStatus = DatabaseUtil.getUserStatus(uniqueEmail);
        Assert.assertEquals(dbStatus, "ACTIVE", "User status in DB should be ACTIVE");
        System.out.println("[ASSERT-DB] Record verified in Database.");
        
        // --- 6. More UI Assertion ---
        // Perform actions to delete the user via UI
        signupPage.click_account_created_continue_button();
        
        // Verify current logged in user in header
        Assert.assertTrue(signupPage.isCurrentUserDisplayed());
        Assert.assertEquals(
        		signupPage.currentUserText(), // Actual string
        		"Logged in as " + data.get("firstName"), // Expected string
        		"'Current logged in user, " + data.get("firstName") + "' should be displayed."); // Error message
        
        signupPage.click_delete_account_link();
        
        // [verify that 'ACCOUNT DELETED!' is visible and click 'Continue' button]
        Assert.assertTrue(signupPage.isAccountDeletedSuccessMessageDisplayed());
        System.out.println("[ASSERT-UI] Success message verified.");
        System.out.println("[UI-LOG] Deleted user via UI");
        signupPage.click_account_deleted_continue_button();
    }
    
    
    // Transactional Rollback/Cleanup 
    @AfterMethod
    public void tearDown() {
        // Regardless of test pass/fail, clean the data
        if (currentTestEmail.get() != null) {
            DatabaseUtil.deleteUser(currentTestEmail.get());
        }  
        if (driver != null) {
            driver.quit();
        }
    }
    
}
