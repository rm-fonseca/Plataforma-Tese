package repository;
import java.util.List;

import io.spring.guides.gs_producing_web_service.Result;

/*
 * Interface of the repositories in the jar compiled independently
 */

public interface RepositoryAbstract {

	public List<Result> SearchByTerm(String term, boolean ignoreExtraProperties);
	public List<Result> SearchByBox(int latitudeFrom,int latitudeTo, int longitudeFrom, int longitudeTo, boolean ignoreExtraProperties);
}
