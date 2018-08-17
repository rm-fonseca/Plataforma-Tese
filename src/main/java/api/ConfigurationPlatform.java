package api;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/*
 * Class that defines the access to the different configuration properties of the platform.
 */
public class ConfigurationPlatform {

	public static Properties prop;

	
	/*
	 * Get configuration value of RelationFields from the properties File.
	 */
	public static String[] getParamtesRelation() {
		List<String> list = new ArrayList<>();

		String fields = prop.getProperty("RelationFields","");
		if (fields.length() == 0)
			return new String[0];

		return fields.split(";");
	}
	
	/*
	 * Get configuration value of CombineFields from the properties File.
	 */
	public static String[] getParamtesCombineFields() {
		List<String> list = new ArrayList<>();

		String fields = prop.getProperty("CombineFields","");
		if (fields.length() == 0)
			return new String[0];

		return fields.split(";");
	}
	
	/*
	 * Get configuration value of CordinatesPointExtraRange from the properties File. Returns 0 if not defined
	 */
	public static int getCordinatesPointExtraRange() {
		
		
		String field = prop.getProperty("CordinatesPointExtraRange", "0");

		return Integer.parseInt(field);
		
		
	}
	
	
	
}
