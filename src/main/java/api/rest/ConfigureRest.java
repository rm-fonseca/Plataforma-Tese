package api.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;



/*
 * Configuration for the Rest Service Api
 */

@Configuration
public class ConfigureRest {
	
	
	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    // Ignore Empty Lists when returning the json object
	    objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
	    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
	    objectMapper.setSerializationInclusion(Include.NON_NULL);

	    return objectMapper;
	}
	
}