package repositoryController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;

/*
 * Calls using threads to the reposiotires.
 */

@EnableAsync
@Configuration
public class AsyncRepositorieConfig {
	
	@Bean
	public AsyncRepositoriesCalls myAsyncRepositoriesCalls() {
		return new AsyncRepositoriesCalls();
	}

	/*
	 * Configuration of the executor of the threads
	 */
	@Bean
	@Qualifier("RepositoriesExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Repositories-");
		executor.initialize();
		return executor;
	}

	
	/*
	 * Class with the calls to the repositories.
	 */
	public class AsyncRepositoriesCalls {

		
		/*
		 * Call the search by box of a repositories in another thread
		 */
		
		@Async("RepositoriesExecutor")
		public CompletableFuture<Boolean> searchRepositorieBox(SearchByBoxRequest request,
				RepositoryContainer container, List<Result> result) {

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());
			try {

				List<Result> repositoriesResult = container.SearchByBox(request.getLatitudeFrom(),
						request.getLatitudeTo(), request.getLongitudeFrom(), request.getLongitudeTo(),
						request.isIgnoreExtraProperties());

				synchronized (result) {
					result.addAll(repositoriesResult);
				}

				System.out.println(
						"Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());
				System.out.flush();

				return CompletableFuture.completedFuture(true);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(
					"Finished Unsucessefully method asynchronously - Name:" + Thread.currentThread().getName());

			return CompletableFuture.completedFuture(false);

		}

		/*
		 * Call the search by term of a repositories in another thread
		 */
		@Async("RepositoriesExecutor")
		public CompletableFuture<Boolean> searchRepositorieTerm(SearchByTermRequest request,
				RepositoryContainer container, List<Result> result) {

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());

			try {

				List<Result> repositoriesResult = container.SearchByTerm(request.getTerm(),
						request.isIgnoreExtraProperties());

				synchronized (result) {
					result.addAll(repositoriesResult);
				}

				System.out.println(
						"Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());

				return CompletableFuture.completedFuture(true);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(
					"Finished Unsucessefully method asynchronously - Name:" + Thread.currentThread().getName());

			return CompletableFuture.completedFuture(false);

		}

	}
}