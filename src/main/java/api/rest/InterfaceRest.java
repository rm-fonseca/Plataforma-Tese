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

import dataController.DataController;
import io.spring.guides.gs_producing_web_service.ListRepositoriesRequest;
import io.spring.guides.gs_producing_web_service.ListRepositoriesResponse;
import io.spring.guides.gs_producing_web_service.Repository;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByBoxResponse;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermResponse;
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
		List<Result> results = DataController.Search(request);


		SearchByTermResponse response = new SearchByTermResponse();
		response.getResults().addAll(results);
		response.setCount(results.size());
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
		List<Result> results = DataController.SearchBox(box);
		
		SearchByBoxResponse response = new SearchByBoxResponse();
		response.getResults().addAll(results);
		response.setCount(results.size());
		return response;
	}
	
	/*
	 * List all repositories available in the platform
	 */

	@RequestMapping(value = "listRepositories", method = RequestMethod.GET, produces = "application/json")
	public ListRepositoriesResponse listRepositories(ListRepositoriesRequest request) {

		List<Repository> results = RepositoryController.ListRepositories();

		ListRepositoriesResponse response = new ListRepositoriesResponse();
		response.getRepositories().addAll(results);
		return response;
	}

}
