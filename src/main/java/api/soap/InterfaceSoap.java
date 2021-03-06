package api.soap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import Log.Log;
import api.AppStarter;
import dataController.DataController;
import plataforma.modelointerno.GetResultRequest;
import plataforma.modelointerno.GetResultResponse;
import plataforma.modelointerno.ListRepositoriesRequest;
import plataforma.modelointerno.ListRepositoriesResponse;
import plataforma.modelointerno.Repository;
import plataforma.modelointerno.Result;
import plataforma.modelointerno.SearchByBoxRequest;
import plataforma.modelointerno.SearchByBoxResponse;
import plataforma.modelointerno.SearchByTermRequest;
import plataforma.modelointerno.SearchByTermResponse;
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

		//Command description to save in the log
		String command = "Soap searchByTerm\nTerm: " + request.getTerm() + "\n";
		command += "DisableCombine: " + request.isDisableCombine() + "\n";
		command += "isDisableRelation: " + request.isDisableRelation() + "\n";
		command += "Repositories :";
		
		for(int i = 0; i < request.getRepositories().size() ; i++) {
			command+= request.getRepositories().get(i) + " ";

		}
		
		
		Log log = new Log(command);

		//Work the data
		List<Result> results = DataController.Search(request, log);
		SearchByTermResponse response = new SearchByTermResponse();
		response.getResults().addAll(results);

		response.getResults().addAll(results);
		response.setCount(results.size());
		
		//Register end of call and write to log file
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}
	/*
	 * Search by coordinates, receiving 2 values to latitude and 2 values to longitude to define a box.
	 * Optionally can be chosen what repositories to search.
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchByBoxRequest")
	@ResponsePayload
	public SearchByBoxResponse search(@RequestPayload SearchByBoxRequest request) {

		//Command description to save in the log
		String command = "Soap searchByBox\nLatitudeFrom: " + request.getLatitudeFrom() + "\nLatitudeTo: " + request.getLatitudeTo() + "\n";
		command += "LongitudeFrom: " + request.getLongitudeFrom() +   "\nLongitudeTo: " + request.getLongitudeTo() + "\n";
		command += "DisableCombine: " + request.isDisableCombine() + "\n";
		command += "isDisableRelation: " + request.isDisableRelation() + "\n";
		command += "Repositories :";
		
		for(int i = 0; i < request.getRepositories().size() ; i++) {
			command+= request.getRepositories().get(i) + " ";

		}
		
		Log log = new Log(command);
		
		//Work the data
		List<Result> results = DataController.SearchBox(request,log);
		SearchByBoxResponse response = new SearchByBoxResponse();
		response.getResults().addAll(results);

		response.getResults().addAll(results);
		response.setCount(results.size());
		
		//Register end of call and write to log file
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}
	/*
	 * List all repositories available in the platform
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "listRepositoriesRequest")
	@ResponsePayload
	public ListRepositoriesResponse search(@RequestPayload ListRepositoriesRequest request) {

		Log log = new Log("List Repositories");

		//Get list of repositories.s
		 List<Repository> results = RepositoryController.ListRepositories();

		ListRepositoriesResponse response = new ListRepositoriesResponse();
		response.getRepositories().addAll(results);
		
		
		//Register end of call and write to log file
		log.Close();
		AppStarter.logger.WriteToFile(log);
		return response;
	}
	
	
	/*
	 * Get a result by ID
	 */

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getResult")
	@ResponsePayload
	public GetResultResponse getResult(@RequestPayload GetResultRequest request) {
		
		String command = "Soap getResult\nID: " + request.getId();

		Log log = new Log(command);

		
		 List<Result> results = RepositoryController.getResult(request,log);

		GetResultResponse response = new GetResultResponse();
		response.getResults().addAll(results);
		response.setCount(results.size());
		
		//Register end of call and write to log file
		log.Close();
		AppStarter.logger.WriteToFile(log);
		
		return response;
	}

}