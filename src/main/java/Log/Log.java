package Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.omg.CORBA.Environment;

/*
 * Class log used to register all the steps of all the calls made to the platform.
 */

public class Log {

	String startDate;
	String endDate;
	String command;
	List<String> steps;
	List<String> errors;
	List<String> times;
	SimpleDateFormat sdf;

	public Log(String command) {

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		startDate = sdf.format(new Date());
		this.command = command;
		steps = new ArrayList<String>();
		times = new ArrayList<String>();
		errors = new ArrayList<String>();
	}

	/*
	 * Register the end of the call associated to the log
	 */
	public void Close() {
		endDate = sdf.format(new Date());
	}
	
	/*
	 * Register a step of the call
	 */
	
	public synchronized int newStep(String step) {
		steps.add(step);
		times.add(steps.size() + " - " + sdf.format(new Date()));
		return steps.size();
	}
	
	/*
	 * Register an error associated with a step using the id of that step
	 */

	public synchronized void addError(int stepId, Exception ex) {
		errors.add(stepId + "\n" + getStacktraceFromException(ex));
	}
	
	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getCommand() {
		return command;
	}

	public String getSteps() {

		String stepsString = "";
		int count = 1;

		for (String step : steps) {
			stepsString += count + " - " + step + System.lineSeparator();
			count++;
		}

		return stepsString;
	}

	public String getErrors() {

		String errorsString = "";

		for (String error : errors) {
			errorsString += error + System.lineSeparator();
		}
		return errorsString;
	}
	
	public String getTimers() {

		String timersString = "";

		for (String time : times) {
			timersString += time + System.lineSeparator();
		}
		return timersString;
	}
	/*
	 * Gets the stacktrace from a exception
	 */
	private String getStacktraceFromException(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		ps.close();
		return baos.toString();
	}

}
