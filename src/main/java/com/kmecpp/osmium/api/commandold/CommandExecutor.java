package com.kmecpp.osmium.api.commandold;

import com.kmecpp.osmium.api.CommandSender;

public interface CommandExecutor {

	void execute(CommandSender sender, String label, String[] args);

}
