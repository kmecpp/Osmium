package com.kmecpp.osmium.platform.sponge.event.events;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerChatEvent;

public class SpongePlayerChatEvent implements PlayerChatEvent {

	private MessageChannelEvent.Chat event;

	public SpongePlayerChatEvent(MessageChannelEvent.Chat event) {
		this.event = event;
	}

	@Override
	public MessageChannelEvent.Chat getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
	}

	@Override
	public boolean shouldFire() {
		return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	@Override
	public String getMessage() {
		return event.getMessage().toString();
	}

	@Override
	public void setMessage(String message) {
		event.setMessage(SpongeAccess.getText(message));
	}

	@Override
	public Set<Player> getRecipients() {
		HashSet<Player> recipients = new HashSet<>();
		for (MessageReceiver r : event.getChannel().get().getMembers()) {
			recipients.add(SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) r));
		}
		return recipients;
	}

}
