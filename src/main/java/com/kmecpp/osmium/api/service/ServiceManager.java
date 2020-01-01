package com.kmecpp.osmium.api.service;

import java.util.HashSet;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class ServiceManager {

	private static HashSet<Class<? extends Service>> classes = new HashSet<>();
	private static HashSet<Service> services = new HashSet<>();

	public static void startPriority() {
		OsmiumLogger.info("Starting priority services");
		start(PriorityService.class);
	}

	public static void start() {
		OsmiumLogger.info("Starting services");
		start(Service.class);
	}

	public static void initialize(Class<?> cls) {
		//		for (Class<?> c : Reflection.getClasses(OsmiumProperties.getPluginPackage())) {
		//			if (Reflection.isAssignable(c, Service.class)) {
		//				classes.add((Class<? extends Service>) c);
		//			}
		//		}
	}

	public static void start(Class<? extends Service> serviceType) {
		for (Class<? extends Service> cls : classes) {
			if (!hasInterface(cls, serviceType)) {
				continue;
			}
			try {
				@SuppressWarnings("deprecation")
				Service service = (Service) cls.newInstance();
				//				Osmium.getEventManager().registerEvents(service);
				services.add(service);
				service.start();
			} catch (Exception e) {
				OsmiumLogger.warn("Could not start service: '" + cls.getName() + "'");
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Service> HashSet<T> getServices(Class<T> serviceType) {
		HashSet<T> services = new HashSet<>();
		for (Service service : getServices()) {
			if (hasInterface(service.getClass(), serviceType)) {
				services.add((T) service);
			}
		}
		return services;
	}

	public static Service getInstance(Class<? extends Service> cls) {
		for (Service service : services) {
			if (service.getClass() == cls) {
				return service;
			}
		}
		return null;
	}

	public static HashSet<Service> getServices() {
		return services;
	}

	private static boolean hasInterface(Class<?> cls, Class<? extends Service> service) {
		for (Class<?> iclasss : cls.getInterfaces()) {
			if (iclasss == service) {
				return true;
			}
		}
		return false;
	}

	public static void stop(Service service) {
		//		Osmium.getEventManager().unregister(service);
	}

}
