package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.PopulateChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;

import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.event.ChunkEvent;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongeChunkEvent {

	public static class SpongeChunkPopulateEvent implements ChunkEvent.Populate {

		private PopulateChunkEvent event;

		public SpongeChunkPopulateEvent(PopulateChunkEvent event) {
			this.event = event;
		}

		@Override
		public PopulateChunkEvent getSource() {
			return event;
		}

		@Override
		public Chunk getChunk() {
			return SpongeAccess.getChunk(event.getTargetChunk());
		}

	}

	public static class SpongeChunkLoadEvent implements ChunkEvent.Load {

		private LoadChunkEvent event;

		public SpongeChunkLoadEvent(LoadChunkEvent event) {
			this.event = event;
		}

		@Override
		public LoadChunkEvent getSource() {
			return event;
		}

		@Override
		public Chunk getChunk() {
			return SpongeAccess.getChunk(event.getTargetChunk());
		}

		@Override
		public boolean isNewChunk() {
			return !event.getTargetChunk().isPopulated();
		}

	}

	public static class SpongeChunkUnloadEvent implements ChunkEvent.Unload {

		private UnloadChunkEvent event;

		public SpongeChunkUnloadEvent(UnloadChunkEvent event) {
			this.event = event;
		}

		@Override
		public UnloadChunkEvent getSource() { //setSave(false)? event.getTargetChunk().getWorld().getProperties().setSerializationBehavior(SerializationBehaviors.NONE);
			return event;
		}

		@Override
		public Chunk getChunk() {
			return SpongeAccess.getChunk(event.getTargetChunk());
		}

	}

}
