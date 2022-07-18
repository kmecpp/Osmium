package com.kmecpp.osmium.api.util.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class History<T> implements Iterable<T> {

	private final T[] data;
	private int index; //Points to next empty spot
	private int size;

	@SuppressWarnings("unchecked")
	public History(int length) {
		this.data = (T[]) new Object[length];
	}

	public void add(T element) {
		data[index] = element;
		index = (index + 1) % data.length;
		size = Math.min(data.length, size + 1);
	}

	public T getMostRecent() {
		return get(0);
		//		return index == 0 ? data[data.length - 1] : get(index - 1);
	}

	public T[] getMostRecent(T[] arr) {
		if (arr.length > data.length) {
			throw new IllegalArgumentException("Cannot get more elements than history length: " + arr.length + " > " + data.length);
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i] = get(i);
		}
		return arr;
	}

	public T get(int index) {
		if (index < 0 || index >= data.length) {
			throw new IllegalArgumentException("Require a non negative index and less than the history length (" + data.length + "). Provided index is " + index);
		}
		int offset = this.index - index - 1;
		if (offset < 0) {
			offset += data.length;
		}
		return data[offset];
	}

	public int getLength() {
		return data.length;
	}

	public int getSize() {
		return size;
	}

	public int count(Function<T, Boolean> f) {
		int count = 0;
		for (T t : data) {
			if (f.apply(t)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private int count;
			private int index = History.this.index;

			@Override
			public boolean hasNext() {
				return count < size;
			}

			@Override
			public T next() {
				index--;
				if (index < 0) {
					index = data.length - 1;
				}
				count++;
				return data[index];
			}

		};
	}

	public ArrayList<T> toList() {
		ArrayList<T> result = new ArrayList<>();
		for (T obj : this) { //Need to use the History iterator
			result.add(obj);
		}
		return result;
	}

	@Override
	public String toString() {
		ArrayList<T> list = new ArrayList<>();
		for (T obj : data) {
			list.add(obj);
		}
		return list.toString();
	}

}
