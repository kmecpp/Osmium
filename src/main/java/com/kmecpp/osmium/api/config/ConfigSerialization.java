package com.kmecpp.osmium.api.config;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Supplier;

public class ConfigSerialization {

	private static final HashMap<Class<?>, Supplier<?>> defaults = new HashMap<>();

	private static final Supplier<?> ZERO = () -> 0;

	static {
		//Primitives
		defaults.put(byte.class, ZERO);
		defaults.put(short.class, ZERO);
		defaults.put(int.class, ZERO);
		defaults.put(long.class, ZERO);
		defaults.put(float.class, ZERO);
		defaults.put(double.class, ZERO);
		defaults.put(char.class, ZERO);
		defaults.put(boolean.class, () -> false);

		//Primitive Wrappers & String
		defaults.put(Byte.class, ZERO);
		defaults.put(Short.class, ZERO);
		defaults.put(Integer.class, ZERO);
		defaults.put(Long.class, ZERO);
		defaults.put(Float.class, ZERO);
		defaults.put(Double.class, ZERO);
		defaults.put(Character.class, ZERO);
		defaults.put(Boolean.class, () -> false);
		defaults.put(String.class, () -> "");

		//Lists
		defaults.put(ArrayList.class, ArrayList::new);
		defaults.put(CopyOnWriteArrayList.class, CopyOnWriteArrayList::new);
		defaults.put(LinkedList.class, LinkedList::new);

		//Sets
		defaults.put(TreeSet.class, TreeSet::new);
		defaults.put(HashSet.class, HashSet::new);
		defaults.put(LinkedHashSet.class, LinkedHashSet::new);
		defaults.put(ConcurrentSkipListSet.class, ConcurrentSkipListSet::new);
		defaults.put(CopyOnWriteArraySet.class, CopyOnWriteArraySet::new);

		//Maps
		defaults.put(HashMap.class, HashMap::new);
		defaults.put(LinkedHashMap.class, LinkedHashMap::new);
		defaults.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
		defaults.put(ConcurrentSkipListMap.class, ConcurrentSkipListMap::new);

		//Queues and deques
		defaults.put(SynchronousQueue.class, SynchronousQueue::new);
		defaults.put(PriorityQueue.class, PriorityQueue::new);
		defaults.put(PriorityBlockingQueue.class, PriorityBlockingQueue::new);
		defaults.put(LinkedBlockingQueue.class, LinkedBlockingQueue::new);
		defaults.put(LinkedBlockingDeque.class, LinkedBlockingDeque::new);
		defaults.put(LinkedTransferQueue.class, LinkedTransferQueue::new);
		defaults.put(ConcurrentLinkedQueue.class, ConcurrentLinkedQueue::new);
		defaults.put(ConcurrentLinkedDeque.class, ConcurrentLinkedDeque::new);
		defaults.put(ArrayDeque.class, ArrayDeque::new);

		defaults.put(Vector.class, Vector::new);
		defaults.put(Stack.class, Stack::new);
		defaults.put(UUID.class, UUID::randomUUID);

		//Interfaces
		defaults.put(List.class, ArrayList::new);
		defaults.put(Map.class, HashMap::new);
		defaults.put(Set.class, HashSet::new);
		defaults.put(Queue.class, LinkedList::new);
		defaults.put(Deque.class, LinkedList::new);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getDefaultFor(Class<T> cls, boolean required) {
		Supplier<T> def = (Supplier<T>) defaults.get(cls);
		if (def == null && required) {
			throw new RuntimeException("The class: " + cls.getName() + " does not have a default config value registered!");
		}
		return def != null ? def.get() : null;
	}

}
