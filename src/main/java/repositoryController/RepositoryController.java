package repositoryController;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import Log.Log;
import api.AppStarter;
import dataController.AsyncDataController;
import dataController.DataController;
import plataforma.modelointerno.GetResultRequest;
import plataforma.modelointerno.LanguageString;
import plataforma.modelointerno.Repository;
import plataforma.modelointerno.Result;
import plataforma.modelointerno.SearchByBoxRequest;
import plataforma.modelointerno.SearchByTermRequest;
import repositoryController.AsyncRepositorieConfig.AsyncRepositoriesCalls;

@Component
public class RepositoryController {

	/*
	 * List of loaded Repositories
	 */
	static Map<Integer, RepositoryContainer> repositories;

	/*
	 * Divides the search by term for all the repositories selected
	 */
	public static List<Result> Search(SearchByTermRequest request, Log log) {

		List<Result> result = new ArrayList<>();

		/*
		 * Threads created for each repositorie
		 */
		List<Future<Boolean>> threads = new ArrayList<>();

		/*
		 * Threads configuration load
		 */
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				AsyncRepositorieConfig.class);
		AsyncRepositoriesCalls bean = context.getBean(AsyncRepositoriesCalls.class);
		System.out.printf("calling async method from thread: %s%n",
				Thread.currentThread().getName() + Thread.currentThread().getId());

		/*
		 * Verifie if the user selected any repositories
		 */
		if (request.getRepositories() == null || request.getRepositories().size() == 0)

			// Call Search by term on all repositories that are allowed to
			for (RepositoryContainer container : repositories.values()) {
				if (container.getRepository().isSearchByTerm())
					threads.add(bean.searchRepositorieTerm(request, container, result, log));

			}
		else
			// Call Search by term on all repositories selected by the user that are allowed
			// to
			for (int repositorieID : request.getRepositories()) {
				RepositoryContainer container = repositories.get(repositorieID);

				if (container == null || !container.getRepository().isSearchByTerm())
					continue;

				threads.add(bean.searchRepositorieTerm(request, container, result, log));

			}

		// Wait for all the threads to finishe processing the requests.
		CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
		context.close();
		return result;
	}

	/*
	 * Divides the search by box for all the repositories selected
	 */
	public static List<Result> SearchBox(SearchByBoxRequest request, Log log) {

		List<Result> result = new ArrayList<>();

		/*
		 * Threads created for each repositorie
		 */
		List<Future<Boolean>> threads = new ArrayList<>();

		/*
		 * Threads configuration load
		 */
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				AsyncRepositorieConfig.class);
		AsyncRepositoriesCalls asyncCalls = context.getBean(AsyncRepositoriesCalls.class);
		System.out.printf("calling async method from thread: %s%n",
				Thread.currentThread().getName() + Thread.currentThread().getId());

		/*
		 * Verifie if the user selected any repositories
		 */
		if (request.getRepositories() == null || request.getRepositories().size() == 0)
			// Call Search by box on all repositories that are allowed to
			for (RepositoryContainer container : repositories.values()) {
				if (container.getRepository().isSearchByBox())
					threads.add(asyncCalls.searchRepositorieBox(request, container, result, log));

			}
		else
			// Call Search by box on all repositories selected by the user that are allowed
			// to
			for (int repositorieID : request.getRepositories()) {
				RepositoryContainer container = repositories.get(repositorieID);

				if (container == null || !container.getRepository().isSearchByBox())
					continue;

				threads.add(asyncCalls.searchRepositorieBox(request, container, result, log));

			}
		// Wait for all the threads to finishe processing the requests.
		CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
		context.close();

		return result;
	}

	/*
	 * Returns a list with information of the repositories
	 */

	public static List<Repository> ListRepositories() {

		List<Repository> reps = new ArrayList<Repository>();

		for (RepositoryContainer container : repositories.values()) {
			reps.add(container.getRepository());
		}

		return reps;
	}

	/*
	 * Loads all the repositories jars to the platform into the repositories list
	 */
	public static void getRepositories() {

		Log log = new Log("Load Repositories Jar");

		repositories = new HashMap<Integer, RepositoryContainer>();

		File dir = new File("Repositorios");
		// Find all files in folder Repositories with the extension .jar
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		for (File file : files) {

			int stepId = log.newStep("Add jar " + file.getName());

			RepositoryContainer repCont;
			try {
				repCont = new RepositoryContainer(file);
				repositories.put(repCont.getRepository().getID(), repCont);
			} catch (Exception e) {

				log.addError(stepId, e);

			}

		}

		log.Close();
		AppStarter.logger.WriteToFile(log);

	}

	public static List<Result> getResult(GetResultRequest request, Log log) {

		List<Result> results = new ArrayList<Result>();

		for (String resultId : request.getId()) {

			String[] ids = resultId.split("&&");

			if (ids.length == 0)
				continue;

			Result result = getResultFromRepository(ids[0], log, request.isIgnoreExtraProperties());

			for (int i = 1; i < ids.length; i++) {
				Result result2 = getResultFromRepository(ids[0], log, request.isIgnoreExtraProperties());

				DataController.joinResults(result, result2);

			}
			results.add(result);
		}
		return results;

	}

	private static Result getResultFromRepository(String request, Log log, boolean ignoreExtraProperties) {

		int indexOf = request.indexOf("/");

		log.newStep("Finding Repository");

		if (indexOf == -1 || indexOf == 0 || indexOf + 1 >= request.length()) {
			log.newStep("Wrong ID Format");
			return null;
		}

		String idRepository = request.substring(0, indexOf);

		int id;
		try {
			id = Integer.parseInt(idRepository);
		} catch (NumberFormatException | NullPointerException nfe) {
			log.newStep("Wrong ID Format");
			return null;
		}

		RepositoryContainer repository = repositories.get(id);

		if (repository == null) {
			log.newStep("Wrong Repository ID");

			return null;
		}

		String idResult = request.substring(indexOf + 1, request.length());

		int stepId = log.newStep("Call to " + repository.getRepository().getName());

		try {
			return repository.getResult(idResult, ignoreExtraProperties);
		} catch (Exception e) {
			log.addError(stepId, e);
			return null;
		}
	}

}