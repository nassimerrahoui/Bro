package com.bro.app;

import com.bro.exception.RuntimeExceptionMapper;
import com.bro.filter.GsonProvider;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
public class BroApp extends Application {

    private static Datastore datastore = null;

	@Override
	public Set<Object> getSingletons() {
		Set<Object> sets = new HashSet<>(1);
		sets.add(new HelloService());
		sets.add(new UserService());
		sets.add(new GeolocationService());
		sets.add(new BrotherhoodService());
		return sets;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> sets = new HashSet<>(1);
		sets.add(GsonProvider.class);
		sets.add(RuntimeExceptionMapper.class);
		return sets;
	}

	static Datastore getDatastore(){
	    if(datastore == null)
	        datastore = new MorphiaService().getDatastore();
	    return datastore;
    }
}
