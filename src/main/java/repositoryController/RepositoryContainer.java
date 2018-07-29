package repositoryController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import plataforma.modelointerno.Repository;
import plataforma.modelointerno.Result;
import repository.RepositoryAbstract;

/*
 * Loads the repository from a a jar
 */

public class RepositoryContainer {

	public Class<RepositoryAbstract> getClassToLoad() {
		return classToLoad;
	}

	private Class<RepositoryAbstract> classToLoad;
	private Repository rep;

	@SuppressWarnings("unchecked")
	public RepositoryContainer(File filename) throws ClassNotFoundException, IOException {

		URL[] classLoaderUrls;

		//Load Jar
		classLoaderUrls = new URL[] { filename.toURI().toURL() };
				
		URLClassLoader child = new URLClassLoader(classLoaderUrls);
		classToLoad =  (Class<RepositoryAbstract>) Class.forName("repository.Repositorio", true, child);
		rep = new Repository();

		
		//Load Properties
		Properties prop = new Properties();
		InputStream input = null;
		try {

			
			input = new FileInputStream(
					filename.getPath().substring(0, filename.getPath().toString().length() - 3) + "properties");

			// load a properties file
			prop.load(input);

			rep.setName(prop.getProperty("Name", null));
			
			
			
			rep.setDescription(prop.getProperty("Description", null));

			// TODO if ID null exception

			rep.setID(Integer.parseInt(prop.getProperty("ID", null)));
			rep.setSearchByTerm(Boolean.parseBoolean(prop.getProperty("SearchByTerm", "false")));
			rep.setSearchByBox(Boolean.parseBoolean(prop.getProperty("SearchByBox", "false")));

		} catch (IOException ex) {
			
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
			throw ex;
		} 
		

	}

	/*
	 * Create a new instance of the repository and calls SearchByTerm
	 */
	public List<Result> SearchByTerm(String term, boolean ignoreExtraProperties) throws Exception {
		RepositoryAbstract instance =(RepositoryAbstract) classToLoad.newInstance();
		return instance.SearchByTerm(term, ignoreExtraProperties);
	}

	public Repository getRepository() {
		return rep;
	}

	/*
	 * Create a new instance of the repository and calls SearchByBox
	 */
	public List<Result> SearchByBox(int latitudeFrom, int latitudeTo, int longitudeFrom, int longitudeTo, boolean ignoreExtraProperties)
			throws Exception {
		RepositoryAbstract instance =(RepositoryAbstract) classToLoad.newInstance();
		return instance.SearchByBox(latitudeFrom, latitudeTo, longitudeFrom, longitudeTo, ignoreExtraProperties);
	}

}
