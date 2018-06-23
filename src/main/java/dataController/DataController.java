package dataController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import api.ConfigurationPlatform;
import dataController.AsyncDataController.AsyncDataCalls;
import io.spring.guides.gs_producing_web_service.LanguageString;
import io.spring.guides.gs_producing_web_service.RelationField;
import io.spring.guides.gs_producing_web_service.RelationLocations;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.Location;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;
import repositoryController.AsyncRepositorieConfig;
import repositoryController.RepositoryController;
import repositoryController.AsyncRepositorieConfig.AsyncRepositoriesCalls;

/*
 * This class is responsivel for trying to detect relations between different results. 
 */
public class DataController {

	public static List<Result> Search(SearchByTermRequest request) {

		System.out.print("SearchByTerm Term=" + request.getTerm());

		if (request.getRepositories().size() > 0) {
			System.out.print(" Repositories[ ");

			for (Integer rep : request.getRepositories())
				System.out.print(rep + " ");

			System.out.print("] ");

			System.out.println("IgnoreExtraProperties= " + request.isIgnoreExtraProperties());

		}
		System.out.println();
		List<Result> results = RepositoryController.Search(request);

		workData(results);



		return results;

	}

	public static List<Result> SearchBox(SearchByBoxRequest request) {

		System.out.print("SearchByBox LatitudeFrom=" + request.getLatitudeFrom());
		System.out.print(" LatitudeTo=" + request.getLatitudeTo());
		System.out.print(" LongitudeFrom=" + request.getLongitudeFrom());
		System.out.print(" LongitudeTo=" + request.getLongitudeTo());

		if (request.getRepositories().size() > 0) {
			System.out.print(" Repositories[ ");

			for (Integer rep : request.getRepositories())
				System.out.print(rep + " ");

			System.out.print("] ");

			System.out.println("IgnoreExtraProperties= " + request.isIgnoreExtraProperties());

		}

		System.out.println();
		List<Result> results = RepositoryController.SearchBox(request);

		workData(results);

		return results;

	}
	
	private static void workData(List<Result> results) {


		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				AsyncDataController.class);
		AsyncDataCalls asyncRepositoriesCalls = context.getBean(AsyncDataCalls.class);
		System.out.printf("calling async method from thread: %s%n",
				Thread.currentThread().getName() + Thread.currentThread().getId());

		
		System.out.println("Begin ConfigurationPlatform.getParamtesCombineFields()");
		
		String[]fields = ConfigurationPlatform.getParamtesCombineFields();

		System.out.println("End ConfigurationPlatform.getParamtesCombineFields()");

		
		for (String field : fields)
			asyncRepositoriesCalls.findRelationsFieldCombine(field.split("&"), results);
		
		List<Future<Void>> threads = new ArrayList<>();

		
		
		threads.add(asyncRepositoriesCalls.findRelationsCoordinates(results));
		
		 fields = ConfigurationPlatform.getParamtesRelation();

		for (String field : fields)
			threads.add(asyncRepositoriesCalls.findRelationsField(field, results));
		
		
		
		System.out.println("Start Wait");
		CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
		System.out.println("All Finished");
		
		context.close();
	}
	
	
	

	


}
