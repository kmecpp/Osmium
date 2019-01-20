package com.kmecpp.osmium.platform.sponge;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import com.kmecpp.osmium.api.command.CommandSender;

public class SpongeConsoleCommandRedirect implements CommandSource {

	private CommandSender output;
	private ConsoleSource console;

	public SpongeConsoleCommandRedirect(CommandSender output) {
		this.output = output;
	}

	@Override
	public Tristate getPermissionValue(Set<Context> contexts, String permission) {
		return Tristate.TRUE;
	}

	@Override
	public void sendMessage(Text message) {
		output.sendMessage(message.toString());
	}

	@Override
	public MessageChannel getMessageChannel() {
		return console.getMessageChannel();
	}

	@Override
	public void setMessageChannel(MessageChannel channel) {
		console.setMessageChannel(channel);
	}

	@Override
	public Optional<CommandSource> getCommandSource() {
		return console.getCommandSource();
	}

	@Override
	public SubjectCollection getContainingCollection() {
		return console.getContainingCollection();
	}

	@Override
	public SubjectReference asSubjectReference() {
		return console.asSubjectReference();
	}

	@Override
	public boolean isSubjectDataPersisted() {
		return console.isSubjectDataPersisted();
	}

	@Override
	public SubjectData getSubjectData() {
		return console.getSubjectData();
	}

	@Override
	public SubjectData getTransientSubjectData() {
		return console.getTransientSubjectData();
	}

	@Override
	public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
		return console.isChildOf(contexts, parent);
	}

	@Override
	public List<SubjectReference> getParents(Set<Context> contexts) {
		return console.getParents(contexts);
	}

	@Override
	public Optional<String> getOption(Set<Context> contexts, String key) {
		return console.getOption(contexts, key);
	}

	@Override
	public String getIdentifier() {
		return console.getIdentifier();
	}

	@Override
	public Set<Context> getActiveContexts() {
		return console.getActiveContexts();
	}

	@Override
	public String getName() {
		return console.getName();
	}

}
