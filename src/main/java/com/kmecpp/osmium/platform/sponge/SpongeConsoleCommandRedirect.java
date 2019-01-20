package com.kmecpp.osmium.platform.sponge;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

public class SpongeConsoleCommandRedirect implements CommandSource {

	private CommandSource output;

	public SpongeConsoleCommandRedirect(CommandSource output) {
		this.output = output;
	}

	@Override
	public Tristate getPermissionValue(Set<Context> contexts, String permission) {
		return Tristate.TRUE;
	}

	@Override
	public void sendMessage(Text message) {
		output.sendMessage(message);
	}

	@Override
	public MessageChannel getMessageChannel() {
		return output.getMessageChannel();
	}

	@Override
	public void setMessageChannel(MessageChannel channel) {
		output.setMessageChannel(channel);
	}

	@Override
	public Optional<CommandSource> getCommandSource() {
		return output.getCommandSource();
	}

	@Override
	public SubjectCollection getContainingCollection() {
		return output.getContainingCollection();
	}

	@Override
	public SubjectReference asSubjectReference() {
		return output.asSubjectReference();
	}

	@Override
	public boolean isSubjectDataPersisted() {
		return output.isSubjectDataPersisted();
	}

	@Override
	public SubjectData getSubjectData() {
		return output.getSubjectData();
	}

	@Override
	public SubjectData getTransientSubjectData() {
		return output.getTransientSubjectData();
	}

	@Override
	public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
		return output.isChildOf(contexts, parent);
	}

	@Override
	public List<SubjectReference> getParents(Set<Context> contexts) {
		return output.getParents(contexts);
	}

	@Override
	public Optional<String> getOption(Set<Context> contexts, String key) {
		return output.getOption(contexts, key);
	}

	@Override
	public String getIdentifier() {
		return output.getIdentifier();
	}

	@Override
	public Set<Context> getActiveContexts() {
		return output.getActiveContexts();
	}

	@Override
	public String getName() {
		return output.getName();
	}

}
