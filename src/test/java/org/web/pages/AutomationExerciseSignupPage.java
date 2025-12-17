package org.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.web.utilities.ReadProperties;

public class AutomationExerciseSignupPage 
{
	WebDriver driver;
	
	public AutomationExerciseSignupPage(WebDriver d)
	{
		this.driver = d;
		PageFactory.initElements(d, this); // using Page Factory
	}
	
	// using Page Factory
	@FindBy(css = "[data-qa='signup-name']")
	WebElement signupName;
	
	@FindBy(css = "[data-qa='signup-email']")
	WebElement signupEmail;

	// using Page Factory
	public void enter_signup_name_PF(String name) throws Exception {
		signupName.sendKeys(name);
	}
	
	public void enter_signup_email_PF(String email) throws Exception {
		signupEmail.sendKeys(email);
	}
	
	// using Object Repository
	public void enter_signup_name(String name) throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("signup_name_css")))
				.sendKeys(name);
	}
	
	public void enter_signup_email(String email) throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("signup_name_css")))
				.sendKeys(email);
	}
	
	public void click_signup_btn() throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("signup_btn_css")))
				.click();
	}
	
	public void enter_login_email(String email) throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("login_email_css")))
				.sendKeys(email);
	}
	
	public void enter_login_password(String password) throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("login_password_css")))
				.sendKeys(password);
	}
	
	public void click_login_btn() throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("login_btn_css")))
				.click();
	}

	public void enter_signup_first_name(String firstName) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_first_name_id")))
				.sendKeys(firstName);
	}
	
	public void enter_signup_last_name(String lastName) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_last_name_id")))
				.sendKeys(lastName);
	}
	
	public void enter_signup_password(String password) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_password_id")))
				.sendKeys(password);
	}
	
	public void enter_signup_address1(String address) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_address1_id")))
				.sendKeys(address);
	}
	
	public void select_signup_country(String country) throws Exception {
		WebElement dropdown = driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_country_id")));
		Select countrySelect = new Select(dropdown);
		countrySelect.selectByValue(country);
	}
	
	public void enter_signup_state(String state) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_state_id")))
				.sendKeys(state);
	}
	
	public void enter_signup_city(String city) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_city_id")))
				.sendKeys(city);
	}
	
	public void enter_signup_zipcode(String zipcode) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_zipcode_id")))
				.sendKeys(zipcode);
	}
	public void enter_signup_mobile_number(String mobileNumber) throws Exception {
		driver.findElement(By.id(
				ReadProperties.readElementProperties("signup_mobile_number_id")))
				.sendKeys(mobileNumber);
	}
	
	public void click_create_account_btn() throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("create_account_btn_css")))
				.click();
	}
	
	public Boolean isAccountCreatedSuccessMessageDisplayed() throws Exception {
		WebElement accountCreated = driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("account_created_success_message_css")));		
		String elementText = driver.findElement(By.xpath(
				ReadProperties.readElementProperties("account_created_success_message_text_xpath")))
				.getText();
		
		if (accountCreated.isDisplayed() && elementText.equalsIgnoreCase("Account Created!")) {
			return true;
		}
		return false;
	}
	
	public void click_account_created_continue_button() throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("account_created_continue_btn_css")))
				.click();
	}
	
	public Boolean isCurrentUserDisplayed() throws Exception {
		WebElement currentUser = driver.findElement(By.xpath(
				ReadProperties.readElementProperties("header_current_user_xpath")));
		
		return currentUser.isDisplayed();
	}
	
	public String currentUserText() throws Exception {
		WebElement currentUser = driver.findElement(By.xpath(
				ReadProperties.readElementProperties("header_current_user_xpath")));
		return currentUser.getText();
	}
	
	public void click_delete_account_link() throws Exception {
		driver.findElement(By.xpath(
				ReadProperties.readElementProperties("header_delete_account_link_xpath")))
				.click();
	}
	
	public Boolean isAccountDeletedSuccessMessageDisplayed() throws Exception {
		WebElement accountDeleted = driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("account_deleted_success_message_css")));		
		String elementText = driver.findElement(By.xpath(
				ReadProperties.readElementProperties("account_deleted_success_message_text_xpath")))
				.getText();
				
		if (accountDeleted.isDisplayed() && elementText.equalsIgnoreCase("Account Deleted!")) {
			return true;
		}
		return false;
	}
	
	public void click_account_deleted_continue_button() throws Exception {
		driver.findElement(By.cssSelector(
				ReadProperties.readElementProperties("account_deleted_continue_btn_css")))
				.click();
	}
}

