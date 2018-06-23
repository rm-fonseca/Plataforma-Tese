package dataController;

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

import api.ConfigurationPlatform;
import io.spring.guides.gs_producing_web_service.LanguageString;
import io.spring.guides.gs_producing_web_service.Location;
import io.spring.guides.gs_producing_web_service.RelationField;
import io.spring.guides.gs_producing_web_service.RelationLocations;
import io.spring.guides.gs_producing_web_service.Result;
import repositoryController.AsyncRepositorieConfig.AsyncRepositoriesCalls;

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
		CompletableFuture<Void> findRelationsCoordinates(List<Result> resultList) {

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());

			boolean latitudeIsIn = false;
			float max, min;

			for (int i = 0; i < resultList.size() - 1; i++) {

				Result result1 = resultList.get(i);

				if (result1.getLocations() == null || result1.getLocations().size() == 0)
					break;

				for (int j = i + 1; j < resultList.size(); j++) {

					Result result2 = resultList.get(j);
					if (result2.getLocations() == null || result1.getLocations().size() == 0)
						break;

					for (Location location1 : result1.getLocations()) {

						if (location1.getLatitude().size() == 0 || location1.getLongitude().size() == 0)
							continue;

						if (location1.getLatitude().size() == 1) {
							max = location1.getLatitude().get(0) + ConfigurationPlatform.getCordinatesPointExtraRange();
							min = location1.getLatitude().get(0) - ConfigurationPlatform.getCordinatesPointExtraRange();
						} else {
							max = Collections.max(location1.getLatitude())
									+ ConfigurationPlatform.getCordinatesAreaExtraRange();
							min = Collections.max(location1.getLatitude())
									- ConfigurationPlatform.getCordinatesAreaExtraRange();
						}

						for (Location location2 : result2.getLocations()) {

							latitudeIsIn = false;

							// Compare Coordenates
							{

								for (float latitude : location2.getLatitude()) {

									if (inBetween(latitude, min, max)) {
										latitudeIsIn = true;
										break;
									}
								}

								if (!latitudeIsIn)
									break;

								if (location1.getLongitude().size() == 1) {
									max = location1.getLongitude().get(0)
											+ ConfigurationPlatform.getCordinatesPointExtraRange();
									min = location1.getLongitude().get(0)
											- ConfigurationPlatform.getCordinatesPointExtraRange();
								} else {
									max = Collections.max(location1.getLongitude())
											+ ConfigurationPlatform.getCordinatesAreaExtraRange();
									min = Collections.max(location1.getLongitude())
											- ConfigurationPlatform.getCordinatesAreaExtraRange();
								}

								for (float longitude : location2.getLongitude()) {

									if (inBetween(longitude, min, max)) {

										RelationLocations rel1 = new RelationLocations();
										RelationLocations rel2 = new RelationLocations();

										rel1.setTargetResultIdLocation(location2.getId());
										rel1.setThisResultIdLocation(location1.getId());
										rel1.setIdResult(result2.getSourceData().get(0));

										rel2.setTargetResultIdLocation(location1.getId());
										rel2.setTargetResultIdLocation(location1.getId());
										rel2.setIdResult(result1.getSourceData().get(0));

										synchronized (result1) {
											result1.getSameLocationCoordenatesThat().add(rel1);
										}
										synchronized (result2) {
											result2.getSameLocationCoordenatesThat().add(rel2);
										}

										break;
									}
								}

							}

						}
					}

				}

			}

			System.out
					.println("Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());
			System.out.flush();

			return CompletableFuture.completedFuture(null);

		}

		private boolean inBetween(float value, float minValue, float maxValue) {
			return value <= maxValue && value >= minValue;
		}

		@Async("DataControllerExecutor")
		CompletableFuture<Void> findRelationsField(String field, List<Result> resultList) {

			System.out.println("Execute method asynchronously - Name:" + Thread.currentThread().getName() + " ID:"
					+ Thread.currentThread().getId());

			boolean found = false;

			for (int i = 0; i < resultList.size() - 1; i++) {

				Result result1 = resultList.get(i);

				List<LanguageString> result1Lang = findField(field, result1);

				if (result1Lang == null || result1Lang.size() == 0)
					break;

				for (int j = i + 1; j < resultList.size(); j++) {

					Result result2 = resultList.get(j);
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

											RelationField rel1 = new RelationField();
											RelationField rel2 = new RelationField();

											rel1.setFieldName(field);
											rel2.setFieldName(field);

											rel1.setFieldValue(lang1text);
											rel2.setFieldValue(lang1text);

											rel1.setTargetResultId(result2.getSourceData().get(0));
											rel2.setTargetResultId(result1.getSourceData().get(0));

											synchronized (result1) {
												result1.getRelationsByFields().add(rel1);
											}

											synchronized (result2) {
												result2.getRelationsByFields().add(rel2);
											}

											found = true;
											break;
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

				}
			}
			System.out
					.println("Finished Sucessefully method asynchronously - Name:" + Thread.currentThread().getName());
			System.out.flush();

			return CompletableFuture.completedFuture(null);

		}

		public void findRelationsFieldCombine(String[] fields, List<Result> resultList) {
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

						List<LanguageString> result1Lang = findField(field, result1);

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
							joinResults(result1, result2);
							resultList.remove(j);
							j--;
							result1.setIsCombinedResut(true);
						}
					}

				}
			}

		}

		private List<LanguageString> findField(String fieldPath, Result result) {

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
			try {
				for (String field : fieldsNames) {
					values = new ArrayList<>();
					methodName = "get" + field;
					for (Object baseObject : baseObjects) {

						aClass = baseObject.getClass();

						method = aClass.getMethod(methodName);
						value = method.invoke(baseObject);

						/*
						 * if (value instanceof List<?>) if (((List<?>) value).size() > 0 && ((List<?>)
						 * value).get(0) instanceof LanguageString) return (List<LanguageString>) value;
						 */
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

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;

		}

		private void joinResults(Object a, Object b) {

			Field[] allFields = a.getClass().getDeclaredFields();
			Object valueA, valueB;
			Method methodA = null;
			try {

				for (Field field : allFields) {

					String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					try {
						methodA = a.getClass().getMethod("get" + fieldName);
					} catch (NoSuchMethodException e) {
						methodA = a.getClass().getMethod("is" + fieldName);
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

}