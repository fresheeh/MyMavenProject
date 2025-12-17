package org.web.dataproviders;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserDataProvider {

	@DataProvider(name = "registrationData")
	public Object[][] getData() throws IOException {
		
		// Read JSON file
		Gson gson = new Gson();
		FileReader reader = new FileReader("src/test/resources/registration-data.json");
		
		// Convert JSON Array to List of Maps
        List<Map<String, String>> dataList = gson.fromJson(reader, new TypeToken<List<Map<String, String>>>(){}.getType());

        // Convert to 2D Array for TestNG
        Object[][] testData = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            testData[i][0] = dataList.get(i);
        }
        
        return testData;
	}
}
