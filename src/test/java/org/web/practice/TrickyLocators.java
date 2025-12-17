package org.web.practice;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TrickyLocators {	
	
	private WebDriver driver;
	
	@BeforeMethod
	public void setup() {
		driver = new EdgeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
		driver.manage().window().maximize();
	}
	/**
	 * L1: XPath based on stable visible text (Text content locator). Strategy:
	 * Targets the final, stable text visible to the user, regardless of dynamic
	 * parent or sibling attributes. Use Case: Buttons, links, or headers where the
	 * displayed text is guaranteed to be constant. Note: Use normalize-space() to
	 * handle leading/trailing whitespaces.
	 */
	final String btnText = "Continue shopping";
	final By L1_BUTTON_BY_TEXT = By.xpath("//button[normalize-space(text())='" + btnText + "']");
	final String inputPlaceholder = "Search Amazon";
	final By L1_INPUT_BY_PLACEHOLDER = By.xpath("//input[@placeholder='" + inputPlaceholder + "']");

	public void initAmazon(WebDriver d) {
		System.out.println("\n--- Navigating to amazon.com ---");
		d.get("https://www.amazon.com/");

		List<WebElement> shopBtns = d.findElements(L1_BUTTON_BY_TEXT);

		if (!shopBtns.isEmpty()) {
			WebElement shopBtn = shopBtns.get(0); // Get the first and only element

			System.out.println("\nButton that sometimes gets in the way.");
			System.out.println("Found element: " + shopBtn.getTagName());
			System.out.println("Element text: " + shopBtn.getText());

			shopBtn.click();
		}
	}
	
	@Test(enabled = true)
	public void trickyLocatorsL1() {
		System.out.println("\n*** Tricky Locator 1 ***");
		initAmazon(driver);

		WebElement searchBar = driver.findElement(L1_INPUT_BY_PLACEHOLDER);

		searchBar.sendKeys("This is a Selenium Automation Test");
		System.out.println("\nFound element: " + searchBar.getTagName());
		System.out.println("Element placeholder: " + searchBar.getAttribute("placeholder"));
		System.out.println("Element value: " + searchBar.getAttribute("value"));
	}

	/**
	 * L2: CSS Selector targeting stable prefix/suffix of a dynamic attribute using
	 * the '^' (starts-with) operator. Strategy: Assumes the start of a dynamic
	 * ID/class remains stable (e.g., 'item-12345' where 'item-' is stable). Use
	 * Case: Input fields, containers, or elements where the ID is partially dynamic
	 * (e.g., generated session IDs).
	 */
	final By L2_TABLE_ROW_BY_DYNAMIC_ID = By.cssSelector("tr[id^='product']");

	@Test(enabled = true)
	public void trickyLocatorL2() {
		System.out.println("\n*** Tricky Locator 2 ***");
		
		/**
         * Browser window not maximum because footer ad cannot be interacted with, shadowrootmode="closed".
         * If it were shadowrootmode="open", you could use element.getShadowRoot() in Selenium 4+ to gain access.
         * Setting the browser window size is a workaround to no display the ad.
         */
		driver.manage().window().setSize(new Dimension(800, 800));
		driver.get("https://automationexercise.com/products");

		By cartBtnXpath = By.xpath("//div[@class='productinfo text-center']/a");
		By continueBtnXpath = By.xpath("//button[text()='Continue Shopping']");
		By cartDescClass = By.className("cart_description");
		By cartPriceClass = By.className("cart_price");
		By cartQuantClass = By.className("cart_quantity");
		By cartTotalClass = By.className("cart_total");
		By cartDeleteBtnCss = By.cssSelector(".cart_delete a");
		By emptyCartTextboxId = By.id("empty_cart");
		
		driver.findElement(L5_POLO_BRAND_LINK).click();
		
		List<WebElement> addCartBtns = driver.findElements(cartBtnXpath);
		if (!addCartBtns.isEmpty()) {
			for (WebElement btn: addCartBtns) {
				btn.click();
				driver.findElement(continueBtnXpath).click();
			}
		}
		
		driver.findElement(By.partialLinkText("Cart")).click();
		
		Assert.assertFalse(driver.findElement(emptyCartTextboxId).isDisplayed(),
				"FAILED: Cart should be contain products.");
		
		List<WebElement> products = driver.findElements(L2_TABLE_ROW_BY_DYNAMIC_ID);
		if (!products.isEmpty()) {
			for (WebElement p: products) {
				System.out.println("\n*** Product Info ***");
				System.out.println("Description: " + p.findElement(cartDescClass).getText());
				System.out.println("Price: " + p.findElement(cartPriceClass).getText());
				System.out.println("Quantity: " + p.findElement(cartQuantClass).getText());
				System.out.println("Total: " + p.findElement(cartTotalClass).getText());
				
				p.findElement(cartDeleteBtnCss).click();
				System.out.println("*** Products(s) removed from cart. ***");
			}
		}
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		wait.until(ExpectedConditions.visibilityOfElementLocated(emptyCartTextboxId));
		Assert.assertTrue(driver.findElement(emptyCartTextboxId).isDisplayed(),
				"FAILED: Cart should be empty.");
	}

	/**
	 * L3: XPath targeting ARIA roles and other stable accessibility attributes.
	 * Strategy: Leverages the element's functional role, which typically doesn't
	 * change, even if IDs or classes do. Combines with a stable attribute
	 * (data-testid). Use Case: Complex UI components like tab lists, menus, or
	 * dialogs, especially in modern frontend frameworks.
	 */
	final By L3_BY_ARIA_LABEL = By.xpath("//a[@aria-label='main content']");

	@Test(enabled = true)
	public void trickyLocatorL3() {
		System.out.println("\n*** Tricky Locator 3 ***");
		JavascriptExecutor js = (JavascriptExecutor) driver; // Initialize JavascriptExecutor
		initAmazon(driver);

		WebElement skipLink = driver.findElement(L3_BY_ARIA_LABEL);

		System.out.println("\nFound element: " + skipLink.getTagName());
		System.out.println("Element aria-label: " + skipLink.getAttribute("aria-label"));
		System.out.println("Element href: " + skipLink.getAttribute("href"));
		System.out.println("Current URL: " + driver.getCurrentUrl());

		js.executeScript("arguments[0].click();", skipLink);

		String newUrl = driver.getCurrentUrl();
		System.out.println("New URL is: " + newUrl);

		String expectedUrlPart = "#skippedLink";
		if (newUrl.contains(expectedUrlPart)) {
			System.out.println("SUCCESS: New URL contains expected part ('" + expectedUrlPart + "').");
		} else {
			System.out.println("FAILURE: New URL does NOT contain expected part. Verification failed.");
		}

		/*
		 * In a traditional example, the link will be opened in a new window/tab
		 * Set<String> windowHandles = driver.getWindowHandles(); // Get all window
		 * handles String newWindowHandle = null;
		 * 
		 * // Find the new window handle for (String handle : windowHandles) { if
		 * (!handle.equals(originalWindowHandle)) { newWindowHandle = handle; break; } }
		 * 
		 * if (newWindowHandle != null) { driver.switchTo().window(newWindowHandle); //
		 * Switch to the new window/tab
		 * 
		 * // Verify the URL of the new window // Since a skip link often doesn't change
		 * the URL drastically, we'll verify the presence of a known substring. String
		 * newUrl = driver.getCurrentUrl();
		 * System.out.println("Switched to NEW Window. URL is: " + newUrl);
		 * 
		 * String expectedUrlPart = "#skippedLink"; if
		 * (newUrl.contains(expectedUrlPart)) {
		 * System.out.println("SUCCESS: New URL contains expected part ('" +
		 * expectedUrlPart + "')."); } else { System.out.
		 * println("FAILURE: New URL does NOT contain expected part. Verification failed."
		 * ); }
		 * 
		 * // Close the new window/tab and switch back to the original driver.close();
		 * driver.switchTo().window(originalWindowHandle);
		 * System.out.println("Switched back to original window. Current URL: " +
		 * driver.getCurrentUrl());
		 * 
		 * } else {
		 * System.out.println("No new window/tab was detected after the L3 click."); }
		 */
	}

	/**
	 * L4: CSS Selector using multiple, combined stable attributes for high
	 * specificity. Strategy: A highly specific locator combining multiple
	 * attributes (type, name, data-qa) to ensure uniqueness without relying on a
	 * single, potentially fragile ID or class. Use Case: Forms with many similar
	 * input fields (e.g., checkout forms).
	 */
	final By L4_QTY_FIELD_BY_MULTIPLE_ATTRIBUTES = By
			.cssSelector("div.gw-col[id='desktop-grid-1'][data-order-sm='1']");

	@Test(enabled = true)
	public void trickyLocatorL4() {
		System.out.println("\n*** Tricky Locator 4 ***");
		initAmazon(driver);

		WebElement cardItem = driver.findElement(L4_QTY_FIELD_BY_MULTIPLE_ATTRIBUTES);

		System.out.println("\nFound element: " + cardItem.getTagName());
		System.out.println("Element id: " + cardItem.getAttribute("id"));
		System.out.println("Element classes: " + cardItem.getAttribute("class"));
		System.out.println("Element data order: " + cardItem.getAttribute("data-order-sm"));
	}

	/**
	 * L5: XPath traversing backward from a known stable element (the <label>) to
	 * the target element (the <a>). Strategy: Locates the target element by
	 * reference to a nearby, stable element (a descriptive label). Use Case: Forms
	 * where the input itself is dynamic but the corresponding visible label is
	 * stable and unique. Using '.' instead of 'text()' to correctly match the link
	 * content, which includes the text from the nested <span> tag (e.g., "(6)").
	 */
	final By L5_POLO_BRAND_LINK = By.xpath("//h2[text()='Brands']/following-sibling::div//a[contains(., 'Polo')]");

	@Test(enabled = true)
	public void trickLocatorL5() {
		System.out.println("\n*** Tricky Locator 5 ***");
		driver.get("https://automationexercise.com/products");

		WebElement poloLink = driver.findElement(L5_POLO_BRAND_LINK);

		System.out.println("\nFound element: " + poloLink.getTagName());
		System.out.println("Element link: " + poloLink.getAttribute("href"));
	}

	// L6: Shadow DOM Host Locator (the outer element containing the Shadow Root)
	// Hypothetical: A custom search component with a stable ID
	// Targeting the <span> element which is the container for the search button input.
	private static final By L6_SHADOW_HOST = By.id("nav-search-submit-text");

	// L6: Element INSIDE the Shadow Root (uses a simple class)
	// Targeting the input element inside the container above.
	// NOTE: This locator is ONLY used after obtaining the Shadow Root.
	private static final By L6_SHADOW_ELEMENT = By.id("nav-search-submit-button");

	@Test(enabled = true)
	public void trickyLocatorL6() {
		try {
			System.out.println("\n*** Tricky Locator 6 ***");
			initAmazon(driver);

			// 1. Locate the Shadow Host element (the visible container)
			// This must be done with a standard locator (ID, class, etc.)
			// NOTE: This element is from the Light DOM for demonstration purposes.
			WebElement shadowHost = driver.findElement(L6_SHADOW_HOST);
			System.out.println("\nShadow Host element id: " + shadowHost.getAttribute("id"));

			// 2. Get the Shadow Root from the host element
			// The result is a SearchContext, allowing new lookups inside the shadow boundary.
			// WARNING: The element found above is NOT a Shadow Host in Amazon's live DOM.
			// This call will throw a runtime error as there is no Shadow Root attached, but the code demonstrates the structural steps of L6.
			SearchContext shadowRoot = shadowHost.getShadowRoot();
			System.out.println("Accessed the Shadow Root successfully (conceptually).");

			// 3. Find the element INSIDE the Shadow Root using the L6_SHADOW_ELEMENT locator
			WebElement internalInput = shadowRoot.findElement(L6_SHADOW_ELEMENT);
			System.out.println("Successfully found the internal Shadow DOM element: " + internalInput.getTagName());

		} catch (NoSuchElementException e) {
			System.out.println("\nL6 Locator: Could not find the stable Host element.");
		} catch (Exception e) {
			// This exception handles the failure of getShadowRoot() on a Light DOM element
			System.out.println(
			"\nL6 Locator: Failed during Shadow DOM operation. (This is expected since the target element is Light DOM). Error: " + e.getMessage());
		}

	}
	
	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			System.out.println("\n--- Closing Browser ---");
			driver.quit();
		}
	}
	
}
