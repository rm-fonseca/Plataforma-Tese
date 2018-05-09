package dataController;

import java.util.Collections;
import java.util.List;

import io.spring.guides.gs_producing_web_service.LanguageString;
import io.spring.guides.gs_producing_web_service.Relation;
import io.spring.guides.gs_producing_web_service.Result;
import io.spring.guides.gs_producing_web_service.SearchByBoxRequest;
import io.spring.guides.gs_producing_web_service.SearchByTermRequest;
import io.spring.guides.gs_producing_web_service.Result.Locations.Location;
import io.spring.guides.gs_producing_web_service.Result.SameLocationCoordenatesThat;
import io.spring.guides.gs_producing_web_service.Result.SameLocationNameThat;
import repositoryController.RepositoryController;

/*
 * This class is responsivel for trying to detect relations between different results. 
 */
public class DataController {

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

		findRelations(results);

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

		findRelations(results);

		return results;

	}
	
	
	/* 
	 * Detects relations between relations
	 * It compares locations coordinates and names and resists similarities in the result
	 */

	private static void findRelations(List<Result> resultList) {

		boolean latitudeIsIn = false;
		boolean nameIsIn = false;
		float max, min;

		for (int i = 0; i < resultList.size() - 1; i++) {

			Result result1 = resultList.get(i);

			if (result1.getLocations() == null)
				break;

			for (int j = i + 1; j < resultList.size(); j++) {

				Result result2 = resultList.get(j);
				if (result2.getLocations() == null)
					break;

				for (Location location1 : result1.getLocations().getLocation()) {

					if (location1.getLatitude().size() == 1) {
						max = location1.getLatitude().get(0) + 1;
						min = location1.getLatitude().get(0) - 1;
					} else {
						max = Collections.max(location1.getLatitude());
						min = Collections.max(location1.getLatitude());
					}

	
					for (Location location2 : result2.getLocations().getLocation()) {

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

									Relation rel1 = new Relation();
									Relation rel2 = new Relation();

									rel1.setTargetResultIdLocation(location2.getId());
									rel1.setThisResultIdLocation(location1.getId());
									rel1.setIdResult(result2.getSourceData().get(0));

									rel2.setTargetResultIdLocation(location1.getId());
									rel2.setTargetResultIdLocation(location1.getId());
									rel2.setIdResult(result1.getSourceData().get(0));

									if (result1.getSameLocationCoordenatesThat() == null)
										result1.setSameLocationCoordenatesThat(new SameLocationCoordenatesThat());
									if (result2.getSameLocationCoordenatesThat() == null)
										result2.setSameLocationCoordenatesThat(new SameLocationCoordenatesThat());

									result1.getSameLocationCoordenatesThat().getRelation().add(rel2);
									result2.getSameLocationCoordenatesThat().getRelation().add(rel1);

									break;
								}
							}

						}

						// End Compare Coordenates

						for (LanguageString langString1 : location1.getName()) {

							for (LanguageString langString2 : location1.getName()) {

								if (langString1.getLanguage().equalsIgnoreCase(langString2.getLanguage())
										&& langString1.getText().equalsIgnoreCase(langString2.getText())) {

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

					}
				}

			}

		}

	}

	private static boolean inBetween(float value, float minValue, float maxValue) {
		return value <= maxValue && value >= minValue;
	}

}
