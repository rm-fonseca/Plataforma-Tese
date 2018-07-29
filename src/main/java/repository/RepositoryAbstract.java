package repository;
import java.util.List;

import plataforma.modelointerno.Result;

public interface RepositoryAbstract {

	public List<Result> SearchByTerm(String term, boolean ignoreExtraProperties) throws Exception;
	public List<Result> SearchByBox(int latitudeFrom,int latitudeTo, int longitudeFrom, int longitudeTo, boolean ignoreExtraProperties) throws Exception;
}
