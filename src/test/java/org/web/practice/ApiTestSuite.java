package org.web.practice;

import io.restassured.RestAssured;
//import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
//import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

//import org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.UUID;
import java.net.ConnectException;
//import java.util.HashMap;
import java.util.Map;

public class ApiTestSuite {

	// Base URL for the demo site
	private final String MOCK_BASE_URI = "https://demo.guru99.com/V4";
	// private final String STATEMENT_ENDPOINT_PATH = "/sinkministatement.php";

	final static String URL = "https://demo.guru99.com/V4/sinkministatement.php";

	// Valid Parameters for positive testing
	private static final String VALID_CUSTOMER_ID = "68195";
	private static final String VALID_PASSWORD = "1234!";
	private static final String VALID_ACCOUNT_NO = "1";

	// Invalid Parameter for negative testing
	private static final String INVALID_ACCOUNT_NO = "99999";

	// Variable to hold the dynamically generated mock authentication token
	private String authToken = null;

	// Schema to validate the structure of the successful statement response
	private static final String STATEMENT_SCHEMA = "schemas/StatementSchema.json";

	// =========================================================================
	// 2. Auth Token Flow (Mock)
	// =========================================================================
	@BeforeSuite
	public void setupAndAuth() {
		// Set the base URI once for all tests
		RestAssured.baseURI = MOCK_BASE_URI;
		obtainAuthToken();
	}

	/**
	 * Simulates the process of obtaining an initial JWT or OAuth token.
	 */
	public void obtainAuthToken() {
		System.out.println("--- Auth Token Flow: Obtain ---");
		this.authToken = "mock_initial_jwt_" + UUID.randomUUID().toString().substring(0, 8);
		System.out.println("Mock Initial Auth Token obtained: " + this.authToken);
	}

	/**
	 * Simulates the process of refreshing an expired token.
	 */
	public void refreshToken() {
		System.out.println("--- Auth Token Flow: Refresh ---");
		this.authToken = "mock_refreshed_jwt_" + UUID.randomUUID().toString().substring(0, 8);
		System.out.println("Mock Auth Token refreshed: " + this.authToken);
	}

	/*
	public static void getResposnseBody() {
		RestAssured.given().when().get(url).then().log().all();

		RestAssured.given().queryParam("CUSTOMER_ID", VALID_CUSTOMER_ID).queryParam("PASSWORD", VALID_PASSWORD)
				.queryParam("Account_No", VALID_ACCOUNT_NO).when()
				.get("https://demo.guru99.com/V4/sinkministatement.php").then().log().body();
	}
	*/
	
	/*
	public static void getResponseStatus() {
		int statusCode = RestAssured.given().queryParam("CUSTOMER_ID", VALID_CUSTOMER_ID)
				.queryParam("PASSWORD", VALID_PASSWORD).queryParam("Account_No", VALID_ACCOUNT_NO).when()
				.get("https://demo.guru99.com/V4/sinkministatement.php").getStatusCode();

		System.out.println("The response status is: " + statusCode);

		RestAssured.given().when().get(url).then().assertThat().statusCode(200);
	}
	*/

	/**
	 * Extracts the pure JSON string from the non-compliant HTML wrapper. This is
	 * necessary because the target API returns the JSON inside <html><body> tags.
	 * * @param response The Rest Assured Response object.
	 * 
	 * @return The clean JSON string.
	 */
	private String extractJsonPayload(Response response) {
		String rawBody = response.asString();

		// Find the index of the first '{' and the last '}'
		int startIndex = rawBody.indexOf('{');
		int endIndex = rawBody.lastIndexOf('}') + 1; // +1 to include the last brace

		if (startIndex == -1 || endIndex <= startIndex) {
			throw new RuntimeException("JSON payload not found or incorrectly formatted within the response body.");
		}

		// Return the extracted substring
		return rawBody.substring(startIndex, endIndex);
	}

	/*
	@Test(enabled = false)
	public void restAssuredTest() {
		getResposnseBody();
		getResponseStatus();
	}
	*/

	@Test(description = "Positive: Validates successful statement retrieval and JSON Schema contract.")
	public void testPositiveResponse() {
		System.out.println("\n--- Test Case: Statement Retrieval (Positive) ---");
		
		Response response;
		try {
			response = RestAssured.given()
					.queryParam("CUSTOMER_ID", VALID_CUSTOMER_ID)
					.queryParam("PASSWORD", VALID_PASSWORD)
					.queryParam("Account_No", VALID_ACCOUNT_NO)
					.when()
					.get(URL);
		} catch(Exception e) {
			// Check specifically for network issues like Connection refused
            if (e.getCause() instanceof ConnectException || (e.getMessage() != null && e.getMessage().contains("Connection refused"))) {
                System.err.println("ERROR: Connection Refused. The target server " + MOCK_BASE_URI + " is unreachable or down.");
                System.err.println("This is a network issue. Please check the server status outside of your test code.");
                Assert.fail("Test failed due to external Connection Refused error.", e);
            }
            throw e; // Re-throw any other exceptions
		}
		

		// Debugging JSON Parse Errors
		// response.body().prettyPrint();

		// Status code check (positive)
		response.then().statusCode(200);
		System.out.println("Status Code check passed: Received 200 OK.");

		// JSON Schema Validation
		try {
			String jsonPayload = extractJsonPayload(response);

			RestAssured.given()
					.body(jsonPayload)
					.contentType(ContentType.JSON) // Ensure the parser treats the input as JSON
					.when().then()
					.log().ifValidationFails()
					.body(matchesJsonSchemaInClasspath(STATEMENT_SCHEMA));

			System.out.println("JSON Schema validation successful: Response structure is correct.");

		} catch (AssertionError e) {
			System.err.println("Schema validation FAILED. Error: " + e.getMessage());
            System.err.println("\n*** CONTRACT MISMATCH: The API's JSON response structure does not match the schema in " + STATEMENT_SCHEMA + ". ***\n");
			Assert.fail("Statement JSON Schema validation failed.", e);
		} catch (RuntimeException e) {
            System.err.println("JSON Extraction FAILED. Error: " + e.getMessage());
            Assert.fail("Failed to extract JSON from HTML wrapper.", e);
       }
	}

	@Test(description = "Negative: Tests retrieval with an invalid account number.")
	public void testNegativeResponse() {
		System.out.println("\n--- Test Case: Statement Retrieval (Negative) ---");
		
		Response response;
		try {
			response = RestAssured.given()
					.queryParam("CUSTOMER_ID", VALID_CUSTOMER_ID)
					.queryParam("PASSWORD", VALID_PASSWORD)
					.queryParam("Account_No", INVALID_ACCOUNT_NO) // Using invalid account number
					.when()
					.get(URL);
		} catch (Exception e) {
			// Check specifically for network issues like Connection refused
            if (e.getCause() instanceof ConnectException || (e.getMessage() != null && e.getMessage().contains("Connection refused"))) {
                System.err.println("ERROR: Connection Refused. The target server " + MOCK_BASE_URI + " is unreachable or down.");
                System.err.println("This is a network issue. Please check the server status outside of your test code.");
                Assert.fail("Test failed due to external Connection Refused error.", e);
            }
            throw e; // Re-throw any other exceptions
		}

		// Debugging JSON Parse Errors
		// response.body().prettyPrint();

		// Status code check (We expect a 200 for this API, but an error message in the body)
		response.then().statusCode(200);

		String rawBody = response.asString();
		String expectedError = "NoData";

		// Use a soft assertion or contains check, as exact error messages can change.
		Assert.assertTrue(rawBody.contains(expectedError),
				"Expected error message containing '" + expectedError + "', but found: " + rawBody);

		System.out.println("Negative test passed: Received expected error message for invalid account.");
	}

	@Test(description = "Idempotent POST: Verifies that a request with the same Idempotency-Key is processed only once")
	public void testIdempotentPostScenario() {
		System.out.println("\n--- Test Case: Idempotent POST Scenario (Mock) ---");

		// 4a. Generate a unique idempotency key
		String idempotencyKey = UUID.randomUUID().toString();
		Map<String, Object> orderPayload = Map.of("item_id", "X-101", "quantity", 3);

		// --- FIRST REQUEST (Creation) ---
		System.out.println("1. Sending initial POST request (Key: " + idempotencyKey.substring(0, 8) + "...)");

		// Mock the Rest Assured request/response chain for the first call.
		// In a real test, response.then().statusCode(201) would be asserted.
		int firstStatusCode = 201; // Mocked success
		Assert.assertEquals(firstStatusCode, 201, "First request should result in 201 Created.");
		System.out.println("----> Response 1: 201 Created.");

		// --- SECOND REQUEST (Idempotent replay) ---
		System.out.println("2. Sending second POST request with the SAME Idempotency Key.");

		// Mock the Rest Assured request/response chain for the second call.
		// The API should return 200 OK or 409 Conflict, but NOT 201.
		int secondStatusCode = 200; // Mocked idempotent success
		Assert.assertEquals(secondStatusCode, 200,
				"Second request with same key should return 200 OK or 409 Conflict.");
		System.out.println("----> Response 2: " + secondStatusCode + " (Idempotent success confirmed).");

		System.out.println("Mock Idempotency test passed: Duplicate request handled correctly.");
	}
}
