package dataController;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import Log.Log;
import api.ConfigurationPlatform;
import plataforma.modelointerno.LanguageString;
import plataforma.modelointerno.Location;
import plataforma.modelointerno.Point;
import plataforma.modelointerno.RelationField;
import plataforma.modelointerno.RelationLocations;
import plataforma.modelointerno.Result;
import plataforma.modelointerno.ValueField;

/*
 * Class to work data asynchronously 
 */

@EnableAsync
@Configuration
public class AsyncDataController {

	@Bean
	public AsyncDataCalls myAsyncDataCalls() {
		return new AsyncDataCalls();
	}

	/*
	 * Configuration of the executor of the threads
	 */
	@Bean
	@Qualifier("DataControllerExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("DataController-");
		executor.initialize();
		return executor;
	}

	public class AsyncDataCalls {

		/*
		 * Detects relations between relations It compares locations coordinates and
		 * regists similarities in the result
		 */

		@Async("DataControllerExecutor")
		CompletableFuture<Void> findRelationsCoordinates(List<Result> resultList, Log log) {

			int idStep = log.newStep("Relation by Cordinates");

			try {

				// Logs thread starting
				System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
						+ Thread.currentThread().getId());

				for (int i = 0; i < resultList.size() - 1; i++) {

					Result result1 = resultList.get(i);

					if (result1.getLocations() == null || result1.getLocations().size() == 0) // if the result has no
																								// locations associate
																								// ignore it
						break;

					for (int j = i + 1; j < resultList.size(); j++) {

						Result result2 = resultList.get(j);
						if (result2.getLocations() == null || result1.getLocations().size() == 0)
							break;

						for (Location location1 : result1.getLocations()) {

							if (location1.getCoordinates().size() == 0) // for each location if has no coordinates
																		// associated ignore it
								continue;

							Area area1, area2;
							// Get max and min latitude
							if (location1.getCoordinates().size() == 1) {

								Rectangle2D.Float rect = new Rectangle2D.Float(
										location1.getCoordinates().get(0).getLongitude()
												- ConfigurationPlatform.getCordinatesPointExtraRange(),
										location1.getCoordinates().get(0).getLatitude()
												- ConfigurationPlatform.getCordinatesPointExtraRange(),
										ConfigurationPlatform.getCordinatesPointExtraRange() * 2,
										ConfigurationPlatform.getCordinatesPointExtraRange() * 2);

								area1 = new Area(rect);

							} else {

								GeneralPath gp = new GeneralPath();

								boolean firstPoint = true;

								for (Point point : location1.getCoordinates()) {

									if (firstPoint) {
										firstPoint = false;
										gp.moveTo(point.getLongitude(), point.getLatitude());
									}

									gp.lineTo(point.getLongitude(), point.getLatitude());

								}

								gp.closePath();
								area1 = new Area(gp);

							}

							for (Location location2 : result2.getLocations()) {

								{
									boolean isFound = false;

									for (Point point : location2.getCoordinates()) {

										if (area1.contains(point.getLongitude(), point.getLatitude())) {

											RelationLocations rel1 = new RelationLocations();
											RelationLocations rel2 = new RelationLocations();

											rel1.setTargetResultIdLocation(location2.getId());
											rel1.setThisResultIdLocation(location1.getId());
											rel1.setIdResult(result2.getID());

											rel2.setTargetResultIdLocation(location1.getId());
											rel2.setThisResultIdLocation(location2.getId());
											rel2.setIdResult(result1.getID());

											synchronized (result1) {
												result1.getSameLocationCoordenatesThat().add(rel1);
											}
											synchronized (result2) {
												result2.getSameLocationCoordenatesThat().add(rel2);
											}

											isFound = true;
											break;

										}
									}
									if (!isFound && !location2.getCoordinates().isEmpty()) {

										GeneralPath gp = new GeneralPath();

										boolean firstPoint = true;

										for (Point point : location2.getCoordinates()) {

											if (firstPoint) {
												firstPoint = false;
												gp.moveTo(point.getLongitude(), point.getLatitude());
											}

											gp.lineTo(point.getLongitude(), point.getLatitude());

										}

										gp.closePath();
										area2 = new Area(gp);
										area2.intersect(area1);
										if (!area2.isEmpty()) {
											RelationLocations rel1 = new RelationLocations();
											RelationLocations rel2 = new RelationLocations();

											rel1.setTargetResultIdLocation(location2.getId());
											rel1.setThisResultIdLocation(location1.getId());
											rel1.setIdResult(result2.getID());

											rel2.setTargetResultIdLocation(location1.getId());
											rel2.setThisResultIdLocation(location2.getId());
											rel2.setIdResult(result1.getID());

											synchronized (result1) {
												result1.getSameLocationCoordenatesThat().add(rel1);
											}
											synchronized (result2) {
												result2.getSameLocationCoordenatesThat().add(rel2);
											}
										}

									}

								}

							}
						}

					}

				}

				System.out.println(
						"Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());
				System.out.flush();
			} catch (Exception e) {
				log.addError(idStep, e);

			}

			return CompletableFuture.completedFuture(null);

		}

		@Async("DataControllerExecutor")
		CompletableFuture<Void> findRelationsField(String fieldsString, List<Result> resultList, Log log) {

			int idStep = log.newStep("Relation: " + fieldsString);

			String fields[] = fieldsString.split("&");

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());

			try {

				boolean found = false;

				String field;
				boolean[] fieldValidation = new boolean[fields.length];

				for (int i = 0; i < resultList.size() - 1; i++) {

					for (int j = i + 1; j < resultList.size(); j++) {

						for (int fieldValidationCounter = 0; fieldValidationCounter < fields.length; fieldValidationCounter++)
							fieldValidation[fieldValidationCounter] = false;

						Result result1 = resultList.get(i);
						Result result2 = resultList.get(j);

						int fieldCounter;

						RelationField rel1 = new RelationField();
						RelationField rel2 = new RelationField();

						for (fieldCounter = 0; fieldCounter < fields.length; fieldCounter++) {

							field = fields[fieldCounter];

							List<LanguageString> result1Lang;
							result1Lang = findField(field, result1);

							if (result1Lang == null || result1Lang.size() == 0)
								break;

							List<LanguageString> result2Lang = findField(field, result2);

							if (result2Lang == null || result2Lang.size() == 0)
								break;

							found = false;

							for (LanguageString langString1 : result1Lang) {

								for (LanguageString langString2 : result2Lang) {

									if (langString1.getLanguage().equalsIgnoreCase(langString2.getLanguage())) {

										for (String lang1text : langString1.getText()) {

											for (String lang2text : langString2.getText()) {

												if (lang1text.equalsIgnoreCase(lang2text)) {

													ValueField vf = new ValueField();

													vf.setFieldName(field);
													vf.setFieldValue(lang1text);

													rel1.getValueField().add(vf);

													fieldValidation[fieldCounter] = true;
													found = true;
												}

											}
											if (found)
												break;
										}

									}
									if (found)
										break;
								}
								if (found)
									break;
							}

							if (!fieldValidation[fieldCounter])
								break;

							if (fieldCounter == fields.length - 1) {

								rel1.setTargetResultId(result2.getID());
								rel2.setTargetResultId(result1.getID());

								synchronized (result1) {
									result1.getRelationsByFields().add(rel1);
								}

								synchronized (result2) {
									result2.getRelationsByFields().add(rel2);
								}

							}
						}

					}
				}

			} catch (Exception e) {
				log.addError(idStep, e);

			}

			System.out
					.println("Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());
			System.out.flush();

			return CompletableFuture.completedFuture(null);

		}

		/*
		 * Find relations comparing a list o selected fields.
		 */
		public void findRelationsFieldCombine(String fieldsString, List<Result> resultList, Log log) {

			int idStep = log.newStep("Combine: " + fieldsString);

			try {

				String[] fields = fieldsString.split("&");

				boolean found = false;

				String field;
				boolean[] fieldValidation = new boolean[fields.length];

				for (int i = 0; i < resultList.size() - 1; i++) {

					for (int j = i + 1; j < resultList.size(); j++) {

						for (int fieldValidationCounter = 0; fieldValidationCounter < fields.length; fieldValidationCounter++)
							fieldValidation[fieldValidationCounter] = false;

						Result result1 = resultList.get(i);
						Result result2 = resultList.get(j);

						int fieldCounter;

						for (fieldCounter = 0; fieldCounter < fields.length; fieldCounter++) {

							field = fields[fieldCounter];

							List<LanguageString> result1Lang;

							result1Lang = findField(field, result1);

							if (result1Lang == null || result1Lang.size() == 0)
								break;

							List<LanguageString> result2Lang = findField(field, result2);

							if (result2Lang == null || result2Lang.size() == 0)
								break;

							found = false;

							for (LanguageString langString1 : result1Lang) {

								for (LanguageString langString2 : result2Lang) {

									if (langString1.getLanguage().equalsIgnoreCase(langString2.getLanguage())) {

										for (String lang1text : langString1.getText()) {

											for (String lang2text : langString2.getText()) {

												if (lang1text.equalsIgnoreCase(lang2text)) {

													fieldValidation[fieldCounter] = true;
													found = true;
												}

											}
											if (found)
												break;
										}

									}
									if (found)
										break;
								}
								if (found)
									break;
							}

							if (!fieldValidation[fieldCounter])
								break;

							if (fieldCounter == fields.length - 1) {
								DataController.joinResults(result1, result2);
								resultList.remove(j);
								j--;
								result1.setIsCombinedResut(true);
							}
						}

					}
				}
			} catch (Exception e) {

				log.addError(idStep, e);

			}

		}

		/*
		 * Find a field using a string with a path of field properties
		 */
		private List<LanguageString> findField(String fieldPath, Result result) throws NoSuchMethodException,
				SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

			if (fieldPath.length() == 0)
				return null;

			String[] fieldsNames = fieldPath.split("-");
			Class aClass;
			Method method;
			Object value;
			String methodName;
			List<Object> values = null, baseObjects;
			baseObjects = new ArrayList<>();
			baseObjects.add(result);

			for (String field : fieldsNames) {
				values = new ArrayList<>();
				methodName = "get" + field;
				for (Object baseObject : baseObjects) {

					aClass = baseObject.getClass();

					method = aClass.getMethod(methodName);
					value = method.invoke(baseObject);

					if (value instanceof List<?>) {
						List<Object> list = (List<Object>) value;

						for (Object valueInList : list) {
							values.add(valueInList);
						}

					} else
						values.add(value);

				}

				baseObjects = values;

			}

			List<LanguageString> languageStringValues = new ArrayList<>();

			for (Object object : values) {
				languageStringValues.add((LanguageString) object);
			}

			return languageStringValues;

		}

	}

}
