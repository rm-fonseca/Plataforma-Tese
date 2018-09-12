package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import plataforma.modelointerno.Result;
import plataforma.modelointerno.SearchByBoxRequest;
import plataforma.modelointerno.SearchByTermRequest;

/*
 * Calls using threads to write to file
 */

@EnableAsync
@Configuration
public class WriteToLogFileAsync {

	@Bean
	public WriteToLogFileAsyncCalls myAsyncRepositoriesCalls() {
		return new WriteToLogFileAsyncCalls();
	}

	/*
	 * Configuration of the executor of the threads
	 */
	@Bean
	@Qualifier("LogExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(8);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Log-");
		executor.initialize();
		return executor;
	}

	public class WriteToLogFileAsyncCalls {

		/*
		 * Writes log to file
		 */
		@Async("LogExecutor")
		public void ThreadWriteToFile(Log log, File file) {
			

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());
			
			synchronized (file) {
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(file,true));
					writer.write("\"" + log.getStartDate().replace("\"","\"\"") + "\",\"" + log.getEndDate().replace("\"","\"\"") + "\",\"" 
					+ log.getCommand().replace("\"","\"\"") + "\",\""
							+ log.getSteps().replace("\"","\"\"") + "\",\""
									+ log.getTimers().replace("\"","\"\"") + "\",\"" + log.getErrors().replace("\"","\"\"") + "\"\n");
					
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
}