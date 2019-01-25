package com.kmecpp.osmium.api.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
	private final ArrayList<Class<?>> tables = new ArrayList<>();
	private final ArrayList<AbstractSingleColumnStandardBasicType<?>> types = new ArrayList<>();
	private final HashMap<Class<?>, String> typeKeys = new HashMap<>();
	private final HashMap<String, String> properties = new HashMap<>();

	private StandardServiceRegistry registry;
	private SessionFactory sessionFactory;
	private boolean initialized;

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setProperty(String property, String value) {
		properties.put(property, value);
	}

	public void initialize() {
		if (initialized) {
			throw new IllegalStateException("Database already initialized!");
		}
		plugin.info("Establishing database connection...");
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
		Map<String, String> settings = new HashMap<>();
		settings.put(Environment.DRIVER, "org.sqlite.JDBC");
		settings.put(Environment.URL, "jdbc:sqlite:" + (plugin == null ? "test.db" : plugin.getFolder().resolve("plugin.db").toString()));
		settings.put(Environment.DIALECT, "org.hibernate.dialect.SQLiteDialect");
		settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
		settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		settings.put(Environment.HBM2DDL_AUTO, "update");
		settings.put(Environment.SHOW_SQL, "true");

		settings.put("hibernate.hikari.connectionTimeout", "5000");
		settings.put("hibernate.hikari.minimumIdle", "2");
		settings.put("hibernate.hikari.maximumPoolSize", "10");
		settings.put("hibernate.hikari.idleTimeout", "300000");
		//		settings.put("dataSourceClassName", "com.zaxxer.hikari.HikariDataSource");
		settings.putAll(properties);

		registryBuilder.applySettings(settings);
		this.registry = registryBuilder.build();

		MetadataSources sources = new MetadataSources(registry);
		for (Class<?> table : tables) {
			plugin.info("Registering database table: " + table.getName());
			sources.addAnnotatedClass(table);
		}

		MetadataBuilder builder = sources.getMetadataBuilder();
		for (AbstractSingleColumnStandardBasicType<?> type : types) {
			builder.applyBasicType(type);
		}
		Metadata metadata = builder.build();
		//		for (AbstractSingleColumnStandardBasicType<?> type : types) {
		//			((MetadataImplementor) metadastota).getTypeResolver().registerTypeOverride(type);
		//		}

		//		System.out.println("RRE: " + metadata.getTypeDefinition("osmium_test_type"));
		//		Type result = ((MetadataImplementor) metadata).getTypeConfiguration().getTypeResolver().heuristicType("osmium_test_type");
		//		System.out.println("RESULT: " + result);
		sessionFactory = metadata.getSessionFactoryBuilder().build();
		initialized = true;
	}

	public ArrayList<Class<?>> getTables() {
		return tables;
	}

	public void addTable(Class<?> cls) {
		tables.add(cls);
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
		//		needsUpdate = true;
	}

	public String getTypeKey(Class<?> cls) {
		return typeKeys.get(cls);
	}

	//	@SuppressWarnings("deprecation")
	//	public void rebuildSessionFactory() {
	//		if (sessionFactory != null) {
	//			sessionFactory.close();
	//		}
	//	}

	public Session getSession() {
		if (sessionFactory.getCurrentSession().isOpen()) {
			return sessionFactory.getCurrentSession();
		}
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
		return query(s -> s.createQuery(query).getResultList());
	}

	public List<?> query(Class<?> entity, String query) {
		return query(s -> s.createQuery(withTable(entity, query)).getResultList());
	}

	public int update(String update, Object... params) {
		return query(s -> {
			Query q = s.createQuery(update);
			for (int i = 0; i < params.length; i++) {
				q.setParameter(i, params[i]);
			}
			return q.executeUpdate();
		});
	}

	public int updateAsync(String update) {
		//		return run(s -> s.createQuery(update).executeUpdate());
		throw new UnsupportedOperationException();
	}

	public void save(Object obj) {
		send((s) -> s.saveOrUpdate(obj));
		//		Session session = sessionFactory.openSession();
		//		session.beginTransaction();
		//		session.saveOrUpdate(obj);
		//		session.getTransaction().commit();
		//		session.close();
	}

	public void increment(Class<?> entity, String column) {
		update(withTable(entity, "update {table} set ? = ? + 1"), column, column);
	}

	public void add(Class<?> entity, String column, int amount) {
		update(withTable(entity, "update {table} set ? = ? + ?"), column, column, amount);
	}

	private static String withTable(Class<?> entity, String query) {
		String name = entity.getAnnotation(Entity.class).name();
		String tableName = name.isEmpty() ? entity.getSimpleName() : name;
		return query.replace("{table}", tableName);
	}

	public <T> List<T> sort(Class<T> entity, String column, OrderBy order, int limit) {
		return sort(entity, column, order, limit, 0);
	}

	public <T> List<T> sort(Class<T> entity, String column, OrderBy order, int limit, int offset) {
		return query(s -> {
			CriteriaBuilder builder = s.getCriteriaBuilder();
			CriteriaQuery<T> query = builder.createQuery(entity);
			if (order == OrderBy.ASC) {
				query.orderBy(builder.asc(query.from(entity).get(column)));
			} else {
				query.orderBy(builder.desc(query.from(entity).get(column)));
			}
			return s.createQuery(query).setMaxResults(limit).getResultList();
		});
	}

	public <T> List<T> diff(Class<T> entity, String first, String second, int limit) {
		return diff(entity, first, second, limit, 0);
	}

	public <T> List<T> diff(Class<T> entity, String first, String second, int limit, int offset) {
		return query(s -> {
			CriteriaBuilder c = s.getCriteriaBuilder();
			CriteriaQuery<T> query = c.createQuery(entity);
			Root<T> root = query.from(entity);
			query.orderBy(c.desc(c.diff(root.get(first), root.get(second))));
			return s.createQuery(query).setFirstResult(offset).setMaxResults(limit).getResultList();
		});
	}

	public <T> T getOrDefault(Class<T> cls, Serializable id, T def) {
		T result = query((s) -> {
			return s.get(cls, id);
			//			T data = s.get(cls, id);
			//			if (data != null) {
			//				return data;
			//			} else {
			//				s.save(def);
			//				return def;
			//			}
		});
		return result != null ? result : def;
	}

	public <T> T getIgnoreCaseOrDefault(Class<T> entity, String column, String id, T def) {
		T result = query((s) -> {
			CriteriaBuilder c = s.getCriteriaBuilder();
			CriteriaQuery<T> query = c.createQuery(entity);
			Root<T> root = query.from(entity);
			query.where(c.equal(c.lower(root.get(column)), id));
			return s.get(entity, id);
		});
		return result != null ? result : def;
	}

	public <T> T get(Class<T> tableClass, Serializable id, Serializable... ids) {
		return query(session -> {
			if (ids.length == 0) {
				return session.get(tableClass, id);
			} else {
				Serializable[] full_ids = new Serializable[ids.length + 1];
				full_ids[0] = id;
				System.arraycopy(ids, 0, full_ids, 1, ids.length);
				return session.byMultipleIds(tableClass).multiLoad(full_ids).get(0);
			}
		});
	}

	public <T> T load(Class<T> tableClass, Serializable id, Serializable... ids) {
		return query(session -> {
			if (ids.length == 0) {
				return session.load(tableClass, id);
			} else {
				Serializable[] full_ids = new Serializable[ids.length + 1];
				full_ids[0] = id;
				System.arraycopy(ids, 0, full_ids, 1, ids.length);
				return session.byMultipleIds(tableClass).multiLoad(full_ids).get(0);
			}
		});
	}

	public <T> boolean delete(Class<T> entity, Serializable id) {
		return query(s -> {
			T obj = s.get(entity, id);
			if (obj != null) {
				s.delete(obj);
				return true;
			} else {
				return false;
			}

		});
	}

	public void send(Consumer<Session> consumer) {
		query((s) -> {
			consumer.accept(s);
			return null;
		});
	}

	public <T> T query(Function<Session, T> consumer) {
		Session session = null;
		try {
			session = getSession();
			session.beginTransaction();
			T result = consumer.apply(session);
			session.getTransaction().commit();
			return result;
		} catch (Throwable t) {
			if (session != null) {
				session.close();
			}
			throw new RuntimeException(t);
		}
	}

}
