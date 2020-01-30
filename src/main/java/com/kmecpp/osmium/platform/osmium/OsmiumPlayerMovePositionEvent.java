package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.osmium.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.location.Location;

public class OsmiumPlayerMovePositionEvent implements PlayerMovePositionEvent {

	private PlayerMoveEvent event;

	public OsmiumPlayerMovePositionEvent(PlayerMoveEvent e) {
		this.event = e;
	}

	@Override
	public Location getFrom() {
		return event.getFrom();
	}

	@Override
	public Location getTo() {
		return event.getTo();
	}

	@Override
	public void setTo(Location location) {
		event.setTo(location);
	}

	@Override
	public Player getPlayer() {
		return event.getPlayer();
	}

	@Override
	public PlayerMoveEvent getSource() {
		return event;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(true);
	}

	@Override
	public boolean isNewPosition() {
		return true;
	}

}
