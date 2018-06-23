package repositoryController;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import io.spring.guides.gs_producing_web_service.Repository;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;
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
	public static List<Result> Search(SearchByTermRequest request) {

		// TODO TIRAR ISTO DAQUI
		if (repositories == null)
			RepositoryController.getRepositories();

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
					threads.add(bean.searchRepositorieTerm(request, container, result));

			}
		else
			// Call Search by term on all repositories selected by the user that are allowed
			// to
			for (int repositorieID : request.getRepositories()) {
				RepositoryContainer container = repositories.get(repositorieID);

				if (container == null || !container.getRepository().isSearchByTerm())
					continue;

				threads.add(bean.searchRepositorieTerm(request, container, result));

			}

		// Wait for all the threads to finishe processing the requests.
		System.out.println("Start Wait");
		CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
		System.out.println("All Finished");
		context.close();
		return result;
	}

	/*
	 * Divides the search by box for all the repositories selected
	 */
	public static List<Result> SearchBox(SearchByBoxRequest request) {

		// TODO TIRAR ISTO DAQUI
		if (repositories == null)
			RepositoryController.getRepositories();

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
					threads.add(asyncCalls.searchRepositorieBox(request, container, result));

			}
		else
			// Call Search by box on all repositories selected by the user that are allowed
			// to
			for (int repositorieID : request.getRepositories()) {
				RepositoryContainer container = repositories.get(repositorieID);

				if (container == null || !container.getRepository().isSearchByBox())
					continue;

				threads.add(asyncCalls.searchRepositorieBox(request, container, result));

			}
		// Wait for all the threads to finishe processing the requests.
		System.out.println("Start Wait");
		CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
		System.out.println("All Finished");
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

	// Automatize this
	/*
	 * Loads all the repositories jars to the platform into the repositories list
	 */
	public static void getRepositories() {

		repositories = new HashMap<Integer, RepositoryContainer>();

		File dir = new File("Repositorios");
		System.out.println(dir.getAbsolutePath());
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		for (File xmlfile : files) {

			RepositoryContainer repCont;
			try {
				repCont = new RepositoryContainer(xmlfile);
				repositories.put(repCont.getRepository().getID(), repCont);
			} catch (MalformedURLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}