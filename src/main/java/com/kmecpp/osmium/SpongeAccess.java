package com.kmecpp.osmium;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.plugin.SpongePlugin;

public class SpongeAccess {

	public static Text getText(String str) {
		return Text.of(str);
	}

	public static void registerCommand(SpongePlugin plugin, CommandSpec spec, String[] aliases) {
		Sponge.getCommandManager().register(plugin, spec, aliases);
	}

}
