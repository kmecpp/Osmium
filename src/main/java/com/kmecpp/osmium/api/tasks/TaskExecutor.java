package com.kmecpp.osmium.api.tasks;

public interface TaskExecutor<T extends AbstractTask<T>> {

	void execute(T task);

}
