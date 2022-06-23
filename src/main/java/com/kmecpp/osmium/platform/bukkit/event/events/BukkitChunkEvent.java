package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.event.ChunkEvent;
import com.kmecpp.osmium.platform.BukkitAccess;

public class BukkitChunkEvent {

	public static class BukkitChunkLoadEvent implements ChunkEvent.Load {

		private ChunkLoadEvent event;

		public BukkitChunkLoadEvent(ChunkLoadEvent event) {
			this.event = event;
		}

		@Override
		public ChunkLoadEvent getSource() {
			return event;
		}

		@Override
		public Chunk getChunk() {
			return BukkitAccess.getChunk(event.getChunk());
		}

		@Override
		public boolean isNewChunk() {
			return event.isNewChunk();
		}

	}

	public static class BukkitChunkUnloadEvent implements ChunkEvent.Unload {

		private ChunkUnloadEvent event;

		public BukkitChunkUnloadEvent(ChunkUnloadEvent event) {
			this.event = event;
		}

		@Override
		public ChunkUnloadEvent getSource() {
			return event;
		}

		@Override
		public Chunk getChunk() {
			return BukkitAccess.getChunk(event.getChunk());
		}

	}

	public static class BukkitChunkPopulateEvent implements ChunkEvent.Populate {

		private ChunkPopulateEvent event;

		public BukkitChunkPopulateEvent(ChunkPopulateEvent event) {
			this.event = event;
		}

		@Override
		public ChunkPopulateEvent getSource() {
			return event;
		}

		@Override
		public Chunk getChunk() {
			return BukkitAccess.getChunk(event.getChunk());
		}

	}

}
