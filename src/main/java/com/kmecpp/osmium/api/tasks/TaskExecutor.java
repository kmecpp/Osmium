package com.kmecpp.osmium.api.tasks;

public interface TaskExecutor<T extends Task<T>> {

	void execute(T task);

}
