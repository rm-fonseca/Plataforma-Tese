package repository;
import java.util.List;

import plataforma.modelointerno.Result;

/*
 * Interface used to define the comuniation format to the repositories.
 */
public interface RepositoryAbstract {

	public List<Result> SearchByTerm(String term, boolean ignoreExtraProperties) throws Exception;
	public List<Result> SearchByBox(int latitudeFrom,int latitudeTo, int longitudeFrom, int longitudeTo, boolean ignoreExtraProperties) throws Exception;
	public Result getResult(String idResult, boolean ignoreExtraProperties) throws Exception;
}
