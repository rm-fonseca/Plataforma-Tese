package api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/*
 * Spring Starter
 */

@SpringBootApplication
public class AppStarter extends SpringBootServletInitializer {


	public static void main(String[] args) throws IOException {

		// Load Properties

		ConfigurationPlatform.prop = new Properties();
		InputStream input = null;

		input = new FileInputStream("platform.properties");
		ConfigurationPlatform.prop.load(input);

		SpringApplication.run(AppStarter.class, args);
	}
	
	

}
