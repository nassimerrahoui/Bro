package com.bro.app;

import com.bro.entity.Bromance;
import com.bro.entity.Geolocation;
import com.bro.entity.User;
import com.bro.exception.RuntimeExceptionMapper;
import com.bro.filter.GsonProvider;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
public class BroApp extends Application {

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
}
