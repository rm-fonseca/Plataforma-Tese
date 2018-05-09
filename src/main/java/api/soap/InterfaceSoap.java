package api.soap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

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

@Endpoint
public class InterfaceSoap {
	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

	@Autowired
	public InterfaceSoap() {
	}

	
	/*
	 * Search by term.
	 * Optionally can be chosen what repositories to search.
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchByTermRequest")
	@ResponsePayload
	public SearchByTermResponse search(@RequestPayload SearchByTermRequest request) {

		List<Result> results = DataController.Search(request);
		SearchByTermResponse response = new SearchByTermResponse();
		response.getResults().addAll(results);

		response.getResults().addAll(results);
		response.setCount(results.size());
		return response;
	}
	/*
	 * Search by coordinates, receiving 2 values to latitude and 2 values to longitude to define a box.
	 * Optionally can be chosen what repositories to search.
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchByBoxRequest")
	@ResponsePayload
	public SearchByBoxResponse search(@RequestPayload SearchByBoxRequest request) {

		List<Result> results = DataController.SearchBox(request);
		SearchByBoxResponse response = new SearchByBoxResponse();
		response.getResults().addAll(results);

		response.getResults().addAll(results);
		response.setCount(results.size());
		return response;
	}
	/*
	 * List all repositories available in the platform
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "listRepositoriesRequest")
	@ResponsePayload
	public ListRepositoriesResponse search(@RequestPayload ListRepositoriesRequest request) {

		 List<Repository> results = RepositoryController.ListRepositories();

		ListRepositoriesResponse response = new ListRepositoriesResponse();
		response.getRepositories().addAll(results);
		return response;
	}
}