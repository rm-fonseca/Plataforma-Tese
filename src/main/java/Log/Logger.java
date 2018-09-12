package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import Log.WriteToLogFileAsync.WriteToLogFileAsyncCalls;
import repositoryController.AsyncRepositorieConfig;
import repositoryController.AsyncRepositorieConfig.AsyncRepositoriesCalls;

public class Logger {

	/*
	 * Writes to the file log
	 */
	
	private File file;
	WriteToLogFileAsyncCalls threaldWriteFile;

	
	public Logger() throws IOException {
	
		//Creates the log if the same wanst created before
		file = new File("log.csv");

		if (file.createNewFile()) { //if the file was created now, then sets up the headers of the columns
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write("sep=,\n"); // important for the Microsoft Excel
			writer.write("Start Date, End Date, Command, Steps, Times, Error List\n");
			writer.close();
		}

		//used to create threds to write to file.
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WriteToLogFileAsync.class);
		threaldWriteFile = context.getBean(WriteToLogFileAsyncCalls.class);
		context.close();

	}

	/*
	 * Launches thread to write log to file.
	 */
	public void WriteToFile(Log log) {

		threaldWriteFile.ThreadWriteToFile(log, file);

	}

}
