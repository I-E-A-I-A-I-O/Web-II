package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesReader {
	
	List<String> properties = new ArrayList<String>();
	
	public List<String> GetProperties() {
		try {
			InputStream fis = this.getClass().getResourceAsStream("config.properties");
			Properties p = new Properties();
			p.load(fis);
			properties.add(p.getProperty("db.url"));
			properties.add(p.getProperty("db.user"));
			properties.add(p.getProperty("db.password"));
			return properties;
		}catch(IOException e) {
			System.out.print(e.getMessage());
			return properties;
		}
	}
}
