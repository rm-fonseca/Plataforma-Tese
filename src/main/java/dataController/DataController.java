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

	
	/*
	 * Joins the properties of two diferent results.
	 */

	public static void joinResults(Object a, Object b) {

		Field[] allFields = a.getClass().getDeclaredFields();
		Object valueA, valueB;
		Method methodA = null;
		try {

			for (Field field : allFields) {

				String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
				System.out.println(fieldName);
				if (fieldName.equalsIgnoreCase("id") && a instanceof Result && b instanceof Result) {

					((Result) a). setID(((Result) a).getID() + "&&" + ((Result) b).getID());
					continue;
				}
				
				try {
					methodA = a.getClass().getMethod("is" + fieldName);
				} catch (NoSuchMethodException e) {
					methodA = a.getClass().getMethod("get" + fieldName);
				}
				valueA = methodA.invoke(a);
				valueB = methodA.invoke(b);

			

				if (valueB == null)
					continue;
				else if (valueA == null)
					a.getClass().getMethod("set" + fieldName).invoke(a, valueB);
				else if (valueA instanceof List<?>) {
					List<Object> listA = (List<Object>) valueA;
					List<Object> listB = (List<Object>) valueB;

					if (listB.size() == 0)
						continue;

					if (listB.get(0) instanceof LanguageString) {

						List<LanguageString> listALs = (List<LanguageString>) valueA;
						List<LanguageString> listBLs = (List<LanguageString>) valueB;

						List<LanguageString> toAdd = new ArrayList<LanguageString>();

						for (LanguageString languageStringB : listBLs) {
							boolean found = false;
							for (LanguageString languageStringA : listALs) {
								if (languageStringA.getLanguage().equals(languageStringB.getLanguage())) {
									found = true;
									for (String stringB : languageStringB.getText()) {

										if (!languageStringA.getText().contains(stringB))
											languageStringA.getText().add(stringB);

									}

									break;

								}
							}

							if (!found) {
								toAdd.add(languageStringB);
							}

						}

						listA.addAll(toAdd);

					} else
						listA.addAll(listB);
				} else if (BeanUtils.isSimpleValueType(valueA.getClass())) {
				} else {

					joinResults(valueA, valueB);

				}

			}

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
