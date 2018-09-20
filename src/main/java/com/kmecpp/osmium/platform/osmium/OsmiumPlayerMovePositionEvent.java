package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.event.events.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.location.Location;

public class OsmiumPlayerMovePositionEvent implements PlayerMovePositionEvent {

	private PlayerMoveEvent e;

	public OsmiumPlayerMovePositionEvent(PlayerMoveEvent e) {
		this.e = e;
	}

	@Override
	public Location getFrom() {
		return e.getFrom();
	}

	@Override
	public Location getTo() {
		return e.getTo();
	}

	@Override
	public Player getPlayer() {
		return e.getPlayer();
	}

	@Override
	public Object getSource() {
		return e;
	}

	@Override
	public boolean isCancelled() {
		return e.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		e.setCancelled(true);
	}

	@Override
	public boolean isNewPosition() {
		return true;
	}

}
