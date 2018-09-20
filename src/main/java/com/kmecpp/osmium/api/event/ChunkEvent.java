package com.kmecpp.osmium.api.event;

import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;

public interface ChunkEvent extends Event {

	Chunk getChunk();

	default World getWorld() {
		return getChunk().getWorld();
	}

	public static interface Load extends ChunkEvent {

		boolean isNewChunk();

	}

	public static interface Unload extends ChunkEvent {

		//		void setSave(boolean save);

	}

	public static interface Populate extends ChunkEvent {

	}

}
