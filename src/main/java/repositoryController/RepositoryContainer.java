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

import io.spring.guides.gs_producing_web_service.Repository;
import io.spring.guides.gs_producing_web_service.Result;
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
	public RepositoryContainer(File filename) throws MalformedURLException, ClassNotFoundException {

		URL[] classLoaderUrls;
		System.out.println();
		System.out.println(filename.toURI().toURL());
		System.out.println();
		System.out.flush();
		
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
			
			Enumeration<Object> a = prop.keys();
			
			while(a.hasMoreElements())
				System.out.println(a.nextElement().toString());
			
			System.out.println(prop.getProperty("Name", null));
			
			
			
			
			rep.setDescription(prop.getProperty("Description", null));

			// TODO if ID null exception

			rep.setID(Integer.parseInt(prop.getProperty("ID", null)));
			rep.setSearchByTerm(Boolean.parseBoolean(prop.getProperty("SearchByTerm", "false")));
			rep.setSearchByBox(Boolean.parseBoolean(prop.getProperty("SearchByBox", "false")));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/*
	 * Create a new instance of the repository and calls SearchByTerm
	 */
	public List<Result> SearchByTerm(String term, boolean ignoreExtraProperties) throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
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
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		RepositoryAbstract instance =(RepositoryAbstract) classToLoad.newInstance();
		return instance.SearchByBox(latitudeFrom, latitudeTo, longitudeFrom, longitudeTo, ignoreExtraProperties);
	}

}
