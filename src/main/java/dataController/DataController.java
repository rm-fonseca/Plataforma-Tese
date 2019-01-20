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

import Log.Log;
import api.ConfigurationPlatform;
import dataController.AsyncDataController.AsyncDataCalls;
import plataforma.modelointerno.LanguageString;
import plataforma.modelointerno.RelationField;
import plataforma.modelointerno.RelationLocations;
import plataforma.modelointerno.Result;
import plataforma.modelointerno.Location;
import plataforma.modelointerno.SearchByBoxRequest;
import plataforma.modelointerno.SearchByTermRequest;
import repositoryController.AsyncRepositorieConfig;
import repositoryController.RepositoryController;
import repositoryController.AsyncRepositorieConfig.AsyncRepositoriesCalls;

/*
 * This class is responsivel for trying to detect relations between different results. 
 */
public class DataController {

	/*
	 * Makes a request to the repository controller for a search by term and then detects relations between the results
	 */
	public static List<Result> Search(SearchByTermRequest request, Log log) {
		List<Result> results = RepositoryController.Search(request,log);
		workData(results,request.isDisableCombine(),request.isDisableRelation(),log);
		return results;

	}
	/*
	 * Makes a request to the repository controller for a search by box and then detects relations between the results
	 */
	public static List<Result> SearchBox(SearchByBoxRequest request,Log log) {

		List<Result> results = RepositoryController.SearchBox(request, log);

		workData(results,request.isDisableCombine(),request.isDisableRelation(),log);

		return results;

	}

	
	/*
	 * Calls all the different relations methods in different threads.
	 */
	private static void workData(List<Result> results, boolean disableCombine, boolean disableRelations, Log log) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncDataController.class);
		AsyncDataCalls asyncRepositoriesCalls = context.getBean(AsyncDataCalls.class);

		if (!disableCombine) {
			String[] fields = ConfigurationPlatform.getParamtesCombineFields(); // Get the fields to compare from the properties file

			for (String field : fields)
				asyncRepositoriesCalls.findRelationsFieldCombine(field, results,log);

		}

		if (!disableRelations) {
			List<Future<Void>> threads = new ArrayList<>();

			threads.add(asyncRepositoriesCalls.findRelationsCoordinates(results,log)); // Get the fields to compare from the properties file

			String[] fields = ConfigurationPlatform.getParamtesRelation();

			for (String field : fields)
				threads.add(asyncRepositoriesCalls.findRelationsField(field, results,log)); //Call a thread for each group of fields to compare

			CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join(); //wait all threads.
		}


		context.close();
	}

}
