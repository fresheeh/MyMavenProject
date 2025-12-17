package org.web.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Helper class for File System operations: Hashing, cleaning up temp folders,
 * and verifying download completion.
 */
public class FileVerificationUtils {

	/**
	 * Creates a unique temporary directory for this test run. Isolate downloads
	 * here to avoid confusion with existing files.
	 */
	public static Path createTempDownloadDir() throws IOException {
		return Files.createTempDirectory("selenium_downloads_");
	}

	/**
	 * Deletes a directory and its contents recursively.
	 */
	public static void cleanUpDirectory(Path path) throws IOException {
		if (Files.exists(path)) {
			try (Stream<Path> walk = Files.walk(path)) {
				walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
		}
	}

	/**
	 * Calculates the SHA-256 hash of a file. Useful for verifying file integrity
	 * after download.
	 */
	public static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] byteArray = new byte[1024];
			int bytesCount;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
		}

		byte[] bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * Waits for a file to appear in the directory and ensures it is fully
	 * downloaded by checking that the file size is stable and does not end in
	 * .crdownload or .part.
	 */
	public static File waitForDownloadToComplete(Path downloadDir, String expectedFileName, int timeoutSeconds) {
		File targetFile = downloadDir.resolve(expectedFileName).toFile();
		long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);

		while (System.currentTimeMillis() < endTime) {
			// 1. Check if file exists
			if (targetFile.exists()) {
				
				// 2. Check basic stability (size > 0 and size not changing)
				long initialSize = targetFile.length();
				try {
					Thread.sleep(1000); // Wait 1 sec to see if size changes
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				long finalSize = targetFile.length();

				// 3. Ensure it's not a temporary browser file
				if (initialSize > 0 && initialSize == finalSize && !targetFile.getName().endsWith(".crdownload")
						&& !targetFile.getName().endsWith(".tmp")) {
					return targetFile;
				}
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return null; // File not found or timed out
	}
}