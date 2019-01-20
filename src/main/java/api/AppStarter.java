package api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import Log.Logger;
import repositoryController.RepositoryController;

/*
 * Spring Starter Class
 */

@SpringBootApplication
public class AppStarter extends SpringBootServletInitializer {

	public static Logger logger;
	
	
	public static void main(String[] args) throws IOException {

		
		
		
		//Initiate the Logger
		logger = new Logger();
		
		//Load All Repositories to the platform.
		RepositoryController.getRepositories();

		
		// Load Properties File
		ConfigurationPlatform.prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("platform.properties");
		ConfigurationPlatform.prop.load(input);

		SpringApplication.run(AppStarter.class, args);
	}
	
	

}
