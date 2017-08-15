package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.api.CommandSender;

public interface CommandExecutor {

	void execute(CommandSender sender, String label, String[] args);

}
