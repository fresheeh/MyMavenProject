package org.web.utilities;

import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {

	// Simulating a DB Table 'users' in memory
	private static Map<String, Map<String, String>> MOCK_USERS_TABLE = new HashMap<>();

	// Seed Data: Manually insert a user (for login tests or collision checks)
	public static void seedUser(String email, String password, String role) {
		Map<String, String> row = new HashMap<>();
		row.put("email", email);
		row.put("password", password); // In a real Database, password should be hashed.
		row.put("role", role);
		row.put("status", "ACTIVE");

		MOCK_USERS_TABLE.put(email, row);
		System.out.println("[DB-LOG] Seeded user: " + email);
	}

	// Database Assertion: Verify a row exists and matches expected criteria
	public static boolean verifyUserCreated(String email) {
		return MOCK_USERS_TABLE.containsKey(email);
	}

	// Database Assertion: Check specific column value (Post-condition)
	public static String getUserStatus(String email) {
		if (!MOCK_USERS_TABLE.containsKey(email)) {
			return null;
		}

		return MOCK_USERS_TABLE.get(email).get("status");
	}

	// Rollback/Cleanup: Delete the row to reset state
	public static void deleteUser(String email) {
		if (MOCK_USERS_TABLE.containsKey(email)) {
			MOCK_USERS_TABLE.remove(email);
			System.out.println("[DB-LOG] Rollback - Deleted user: " + email);
		}
	}

	// Helper to simulate DB connection check
	public static boolean isDbConnected() {
		return true; // Always true for mock
	}
}
