package com.kmecpp.osmium;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class Database {

	private final OsmiumPlugin plugin;
	private final StandardServiceRegistry registry;
	private final ArrayList<Class<?>> tables = new ArrayList<>();

	private SessionFactory sessionFactory;

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;

		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
		Map<String, String> settings = new HashMap<>();
		settings.put(Environment.DRIVER, "org.sqlite.JDBC");
		settings.put(Environment.URL, "jdbc:sqlite:" + Paths.get("").relativize(plugin.getFolder().resolve("plugin.db")));
		settings.put(Environment.DIALECT, "org.hibernate.dialect.SQLiteDialect");
		settings.put(Environment.HBM2DDL_AUTO, "update");
		registryBuilder.applySettings(settings);
		this.registry = registryBuilder.build();
	}

	public void addTable(Class<?> cls) {
		tables.add(cls);
	}

	public void rebuildSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}

		MetadataSources sources = new MetadataSources(registry);
		for (Class<?> table : tables) {
			sources.addAnnotatedClass(table);
		}

		Metadata metadata = sources.getMetadataBuilder().build();
		sessionFactory = metadata.getSessionFactoryBuilder().build();
		sessionFactory = metadata.getSessionFactoryBuilder().build();
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public Session getSession() {
		return sessionFactory.openSession();
	}

	public void save(Object obj) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.save(obj);
		session.getTransaction().commit();
	}

	public <T> T load(Class<T> tableClass, Serializable id, Serializable... ids) {
		Session session = sessionFactory.getCurrentSession();
		if (ids.length == 0) {
			session.beginTransaction();
			return session.load(tableClass, id);
		} else {
			Serializable[] full_ids = new Serializable[ids.length + 1];
			full_ids[0] = id;
			System.arraycopy(ids, 0, full_ids, 1, ids.length);
			return session.byMultipleIds(tableClass).multiLoad(full_ids).get(0);
		}
	}

}
