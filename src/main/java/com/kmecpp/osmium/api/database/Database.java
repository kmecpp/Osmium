package com.kmecpp.osmium.api.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import com.kmecpp.osmium.api.persistence.Deserializer;
import com.kmecpp.osmium.api.persistence.Serializer;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class Database {

	private final OsmiumPlugin plugin;
	private final StandardServiceRegistry registry;
	private final ArrayList<Class<?>> tables = new ArrayList<>();
	private final ArrayList<AbstractSingleColumnStandardBasicType<?>> types = new ArrayList<>();
	private final HashMap<Class<?>, String> typeKeys = new HashMap<>();

	private SessionFactory sessionFactory;

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;

		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
		Map<String, String> settings = new HashMap<>();
		settings.put(Environment.DRIVER, "org.sqlite.JDBC");
		settings.put(Environment.URL, "jdbc:sqlite:" + (plugin == null ? "test.db" : plugin.getFolder().resolve("plugin.db").toString()));
		settings.put(Environment.DIALECT, "org.hibernate.dialect.SQLiteDialect");
		settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		settings.put(Environment.HBM2DDL_AUTO, "update");
		settings.put(Environment.SHOW_SQL, "true");

		//		settings.put("hibernate.hikari.connectionTimeout", "20000");
		//		settings.put("hibernate.hikari.minimumIdle", "2");
		//		settings.put("hibernate.hikari.maximumPoolSize", "10");
		//		settings.put("hibernate.hikari.idleTimeout", "300000");

		registryBuilder.applySettings(settings);
		this.registry = registryBuilder.build();
	}

	public void addTable(Class<?> cls) {
		tables.add(cls);
	}

	public String getTypeKey(Class<?> cls) {
		return typeKeys.get(cls);
	}

	//	@SuppressWarnings("deprecation")
	public void rebuildSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}

		MetadataSources sources = new MetadataSources(registry);
		for (Class<?> table : tables) {
			sources.addAnnotatedClass(table);
		}

		MetadataBuilder builder = sources.getMetadataBuilder();
		for (AbstractSingleColumnStandardBasicType<?> type : types) {
			builder.applyBasicType(type);
		}
		Metadata metadata = builder.build();
		//		for (AbstractSingleColumnStandardBasicType<?> type : types) {
		//			((MetadataImplementor) metadata).getTypeResolver().registerTypeOverride(type);
		//		}

		//		System.out.println("RRE: " + metadata.getTypeDefinition("osmium_test_type"));
		//		Type result = ((MetadataImplementor) metadata).getTypeConfiguration().getTypeResolver().heuristicType("osmium_test_type");
		//		System.out.println("RESULT: " + result);
		sessionFactory = metadata.getSessionFactoryBuilder().build();
	}

	public Session getSession() {
		return sessionFactory.openSession();
	}

	//	public static class TestType implements Serializable {
	//
	//		private static final long serialVersionUID = 4853198864572677624L;
	//		private double a;
	//
	//		public TestType(double a) {
	//			this.a = a;
	//		}
	//
	//		@Override
	//		public String toString() {
	//			return String.valueOf(a);
	//		}
	//
	//		public static TestType fromString(String str) {
	//			return new TestType(Double.parseDouble(str));
	//		}
	//
	//	}
	//
	//	@Entity(name = "test_table")
	//	public static class TestTable {
	//
	//		@Id
	//		//		private LocalDate date = LocalDate.now();
	//		@org.hibernate.annotations.Type(type = "osmium_test_type")
	//		private TestType test = new TestType(3.14);
	//
	//	}
	//
	//	public static void main(String[] args) {
	//		Database d = new Database(null);
	//		d.addTable(TestTable.class);
	//		d.registerType(TestType.class, "test_type", String::valueOf, TestType::fromString);
	//
	//		d.rebuildSessionFactory();
	//		d.save(new TestTable());
	//		//		d.load(TestTable.class, LocalDate.now());
	//	}

	public List<?> query(String query) {
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery(query).getResultList();
	}

	public int update(String update) {
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery(update).executeUpdate();
	}

	public int updateAsync(String update) {
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery(update).executeUpdate();
	}

	public void save(Object obj) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(obj);
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

	public <T> void registerType(Class<T> typeClass, String key, Serializer<T> serializer, Deserializer<T> deserializer) {
		JavaTypeDescriptor<T> javaTypeDescriptor = new AbstractTypeDescriptor<T>(typeClass) {

			private static final long serialVersionUID = -505731604660159178L;

			@Override
			public T fromString(String string) {
				return deserializer.deserialize(string);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
				return (X) serializer.serialize(value);
			}

			@Override
			public <X> T wrap(X value, WrapperOptions options) {
				return deserializer.deserialize((String) value);
			}

		};

		types.add(new AbstractSingleColumnStandardBasicType<T>(VarcharTypeDescriptor.INSTANCE, javaTypeDescriptor) {

			private static final long serialVersionUID = -7835848990623858505L;

			@Override
			public String getName() {
				return plugin.getName() + "_" + key;
			}

		});
		typeKeys.put(typeClass, key);
	}

}
