package dataController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import api.ConfigurationPlatform;
import io.spring.guides.gs_producing_web_service.LanguageString;
import io.spring.guides.gs_producing_web_service.RelationField;
import io.spring.guides.gs_producing_web_service.RelationLocations;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.Location;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;
import repositoryController.RepositoryController;

/*
 * This class is responsivel for trying to detect relations between different results. 
 */
public class DataController {
	
	
	public static String[] getParamtesRelation(){
		 List<String> list = new ArrayList<>();		 
		 
		 String fields = ConfigurationPlatform.prop.getProperty("RelationFields");
		 if(fields.length() == 0)
			 return new String[0];
		 
		 return fields.split(";");
	}
	

	public static List<Result> Search(SearchByTermRequest request) {

		System.out.print("SearchByTerm Term=" + request.getTerm());
		
		if(request.getRepositories().size() > 0) {
			System.out.print(" Repositories[ ");
			

			for(Integer rep : request.getRepositories())
				System.out.print(rep+" ");
			
			
			System.out.print("] ");

			
			
			System.out.println("IgnoreExtraProperties= " + request.isIgnoreExtraProperties());
			

		}
		System.out.println();
		List<Result> results = RepositoryController.Search(request);

		
		String[] fields = getParamtesRelation();
		
		findRelationsCoordinates(results);
		
		
		for(String field : fields)
			findRelationsField(field,results);
		
		

		return results;

	}

	public static List<Result> SearchBox(SearchByBoxRequest request) {

		
		System.out.print("SearchByBox LatitudeFrom=" + request.getLatitudeFrom());
		System.out.print(" LatitudeTo=" + request.getLatitudeTo());
		System.out.print(" LongitudeFrom=" + request.getLongitudeFrom());
		System.out.print(" LongitudeTo=" + request.getLongitudeTo());
		
		if(request.getRepositories().size() > 0) {
			System.out.print(" Repositories[ ");
			

			for(Integer rep : request.getRepositories())
				System.out.print(rep+" ");
			
			
			System.out.print("] ");

			
			
			System.out.println("IgnoreExtraProperties= " + request.isIgnoreExtraProperties());
			

		}
		
		System.out.println();

		
		List<Result> results = RepositoryController.SearchBox(request);

		findRelationsCoordinates(results);

		return results;

	}
	
	
	/* 
	 * Detects relations between relations
	 * It compares locations coordinates and regists similarities in the result
	 */

	private static void findRelationsCoordinates(List<Result> resultList) {

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

					if (location1.getLatitude().size() == 1) {
						max = location1.getLatitude().get(0) + 1;
						min = location1.getLatitude().get(0) - 1;
					} else {
						max = Collections.max(location1.getLatitude());
						min = Collections.max(location1.getLatitude());
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
								max = location1.getLongitude().get(0) + 1;
								min = location1.getLongitude().get(0) - 1;
							} else {
								max = Collections.max(location1.getLongitude());
								min = Collections.max(location1.getLongitude());
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

					

									result1.getSameLocationCoordenatesThat().add(rel1);
									result2.getSameLocationCoordenatesThat().add(rel2);

									break;
								}
							}

						}



					}
				}

			}

		}

	}

	private static boolean inBetween(float value, float minValue, float maxValue) {
		return value <= maxValue && value >= minValue;
	}
	
	private static void findRelationsField(String field, List<Result> resultList) {
		
		System.out.println("Field "+ field);
		
		for (int i = 0; i < resultList.size() - 1; i++) {

			Result result1 = resultList.get(i);
			
			
			List<LanguageString> result1Lang = findField(field,result1);
			
			if (result1Lang == null || result1Lang.size() == 0)
				break;

			for (int j = i + 1; j < resultList.size(); j++) {

				Result result2 = resultList.get(j);
				List<LanguageString> result2Lang = findField(field,result2);
				
				if (result2Lang == null || result2Lang.size() == 0)
					break;

				for (LanguageString langString1 : result1Lang) {

					for (LanguageString langString2 : result2Lang) {
						
						if (langString1.getLanguage().equalsIgnoreCase(langString2.getLanguage())) {
							
							
							
							for (String lang1text : langString1.getText()) {

								for (String lang2text : langString2.getText()) {
									
									if(lang1text.equalsIgnoreCase(lang2text)) {
										
										RelationField rel1 = new RelationField();
										RelationField rel2 = new RelationField();

										rel1.setFieldName(field);
										rel2.setFieldName(field);
										
										
										rel1.setTargetResultId(result2.getSourceData().get(0));
										rel2.setTargetResultId(result1.getSourceData().get(0));
										
										

										result1.getRelationsByFields().add(rel1);
										result2.getRelationsByFields().add(rel2);
										
									}
									
								}
							}
							
							
						}
						
						
						
					}
					}
				
			}
		}
		
	}
	
	
	private static List<LanguageString> findField(String fieldName, Result result){
		
		
		
		
		
		Class aClass = Result.class;
		try {
			Method field = aClass.getMethod("get" + fieldName);
			Object value = field.invoke(result);

			if(value instanceof List<?>) 
				if(((List<?>) value).size()  > 0 && ((List<?>) value).get(0) instanceof LanguageString) 
					return (List<LanguageString>) value;
			
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

	
	

}



/*
 						// End Compare Coordenates

						for (LanguageString langString1 : location1.getName()) {

							for (LanguageString langString2 : location1.getName()) {

								if (langString1.getLanguage().equalsIgnoreCase(langString2.getLanguage())
										&& langString1.getText().get(0).equalsIgnoreCase(langString2.getText().get(0))) {

									Relation rel1 = new Relation();
									Relation rel2 = new Relation();

									rel1.setTargetResultIdLocation(location2.getId());
									rel1.setThisResultIdLocation(location1.getId());
									rel1.setIdResult(result2.getSourceData().get(0));

									rel2.setTargetResultIdLocation(location1.getId());
									rel2.setThisResultIdLocation(location2.getId());
									rel2.setIdResult(result1.getSourceData().get(0));

									if (result1.getSameLocationNameThat() == null)
										result1.setSameLocationNameThat(new SameLocationNameThat());
									if (result2.getSameLocationNameThat() == null)
										result2.setSameLocationNameThat(new SameLocationNameThat());

									result1.getSameLocationNameThat().getRelation().add(rel1);
									result2.getSameLocationNameThat().getRelation().add(rel2);
									nameIsIn = true;

									break;

								}

							}

							if (nameIsIn)
								break;

						}
						*/
