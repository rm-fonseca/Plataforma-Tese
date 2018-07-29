package api.rest;

import java.util.List;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import Log.Log;
import api.AppStarter;
import dataController.DataController;
import plataforma.modelointerno.ListRepositoriesRequest;
import plataforma.modelointerno.ListRepositoriesResponse;
import plataforma.modelointerno.Repository;
import plataforma.modelointerno.Result;
import plataforma.modelointerno.SearchByBoxRequest;
import plataforma.modelointerno.SearchByBoxResponse;
import plataforma.modelointerno.SearchByTermRequest;
import plataforma.modelointerno.SearchByTermResponse;
import repositoryController.RepositoryController;


/*
 * Interface for Rest Web Services
 * All rest services are under /rest/
 */
@RestController
@RequestMapping(value = "rest")
public class InterfaceRest {

	
	
	/*
	 * Search by term.
	 * Optionally can be chosen what repositories to search.
	 */
	@RequestMapping(value = "searchByTerm", method = RequestMethod.GET, produces = "application/json")
	public SearchByTermResponse search(SearchByTermRequest request,
			@RequestParam(required = false) List<Integer> repositories) {
		 
		
		if (repositories != null)
			request.getRepositories().addAll(repositories);
		
		String command = "Rest searchByTerm\nTerm: " + request.getTerm() + "\n";
		command += "DisableCombine: " + request.isDisableCombine() + "\n";
		command += "isDisableRelation: " + request.isDisableRelation() + "\n";
		command += "Repositories :";
		
		for(int i = 0; i < request.getRepositories().size() ; i++) {
			command+= request.getRepositories().get(i) + " ";

		}
		
		
		Log log = new Log(command);

		
		
		List<Result> results = DataController.Search(request,log);


		SearchByTermResponse response = new SearchByTermResponse();
		response.getResults().addAll(results);
		response.setCount(results.size());
		
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}

	
	
	
	/*
	 * Search by coordinates, receiving 2 values to latitude and 2 values to longitude to define a box.
	 * Optionally can be chosen what repositories to search.
	 */
	@RequestMapping(value = "searchByBox", method = RequestMethod.GET, produces = "application/json")
	public SearchByBoxResponse searchBox(SearchByBoxRequest box,
			@RequestParam(required = false) List<Integer> repositories) {
		if (repositories != null)
			box.getRepositories().addAll(repositories);
		
		String command = "Rest searchByBox\nLatitudeFrom: " + box.getLatitudeFrom() + "\nLatitudeTo: " + box.getLatitudeTo() + "\n";
		command += "LongitudeFrom: " + box.getLongitudeFrom() +   "\nLongitudeTo: " + box.getLongitudeTo() + "\n";
		command += "DisableCombine: " + box.isDisableCombine() + "\n";
		command += "isDisableRelation: " + box.isDisableRelation() + "\n";
		command += "Repositories :";
		
		for(int i = 0; i < box.getRepositories().size() ; i++) {
			command+= box.getRepositories().get(i) + " ";

		}
		
		Log log = new Log(command);

		
		List<Result> results = DataController.SearchBox(box,log);
		
		SearchByBoxResponse response = new SearchByBoxResponse();
		response.getResults().addAll(results);
		response.setCount(results.size());
		
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}
	
	/*
	 * List all repositories available in the platform
	 */

	@RequestMapping(value = "listRepositories", method = RequestMethod.GET, produces = "application/json")
	public ListRepositoriesResponse listRepositories(ListRepositoriesRequest request) {

		
		Log log = new Log("List Repositories");

		
		List<Repository> results = RepositoryController.ListRepositories();

		ListRepositoriesResponse response = new ListRepositoriesResponse();
		response.getRepositories().addAll(results);
		
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}

}
