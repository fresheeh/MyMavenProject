package org.web.utilities;

import java.io.FileReader;
import java.util.Properties;

public class ReadProperties 
{
	public static String readElementProperties(String key) throws Exception
	{
		FileReader fr = new FileReader("./src/test/resources/element.properties");
		Properties props = new Properties();
		
		props.load(fr);
		
		return props.get(key).toString();
	}

}
