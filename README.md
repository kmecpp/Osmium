# Osmium

Osmium is an abstraction layer for Bukkit and Sponge which aims to provide the easiest most concise way to create Minecraft plugins period.

In addition to being able to target multiple different platforms, Osmium has many unique features which drastically reduce the amount of code necessary to complete many common tasks.

Although it will take a very long time to implement enough features to have a complete Minecraft API, Osmium should be in a usable state fairly soon. Plugins requiring any part of a specific platform which Osmium has not yet implemented can simply write platform specific code as needed. Osmium even has easy methods to help you do this.


Join our Discord channel stay updated with the project or help out: https://discord.gg/YSgde2Y 

## Features

 * Platform independent
 * More concise command API and automatic registration of commands
 * More concise listener API and automatic registration of listeners
 * Service API that allows hooking into common server events at any time
 * Super easy configuration handling
 * And much more...
 
<hr>
 
## Status

### Implemented:

* Configs
* Commands
* Listeners
* Schedulers
* Some events

### TODO:
* More events
* More methods
* Inventories
* Everything else...
 
<hr>

## Examples


### Listeners


Registering listeners is as easy as adding an @Listener annotation to a valid method. There's no need to register the listener or implement any interfaces.

```
@Listener
public void onPlayerJoin(PlayerJoinEvent e){
	//Listener will work without any additional code needed
}
```
**Important Note:** The containing class MUST have a default constructor. If however, for whatever reason you do not want to do that, you just have to call plugin.enableEvents(listener) to provide Osmium with an instance of the class.

### Commands

There are a few ways to create commands.

The first way is the shortest and is useful when you only have a few very simple commands

```
Osmium.createCommand("spawn", "home")
	.setDescription("Teleport to the spawn point of your current world")
	.setPermission("myplugin.spawn");
	.setPlayersOnly();
	.onExecute((e) -> {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	})
```

Alternatively you can move the command into its own class. The same command would look like this

```
@Command(
	aliases = { "spawn", "home"},
	description = "Teleport to the spawn point of your current world",
	permission = "myplugin.spawn",
	playersOnly = true
)
public class EnjinNewsCommand extends OsmiumCommand {

	@Override
	public void execute(CommandEvent e) {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	}

}
```

The last way to define a command is by using it as a parent for subcommands.

Take the /spawn command from the previous examples. If we expand its functionality so we have a list of options

Ex:
- /spawn - Teleport to the current world's spawn
- /spawn set - Sets the spawn point of the current world
- /spawn delete - Sets the spawn point of the current world to (0,0,0)

```
@Command(
	aliases = { "spawn", "home"},
	description = "Teleport to the spawn point of your current world",
	permission = "myplugin.spawn",
	playersOnly = true
)
public class EnjinNewsCommand extends OsmiumCommand {

	@Override
	public void execute(CommandEvent e) {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	}
	
	@Command(aliases = "set", admin = true) //The subcommand is already playersOnly from its parent settings
	public void onSet(CommandEvent e){
		Player player = e.getPlayer();
		player.getWorld().setSpawn(player.getLocation());
		player.sendMessage(C.GREEN + "New spawn point set successfully!");
	}
	
	@Command(aliases = "delete", admin = true)
	public void onDelete(CommandEvent e){
		Player player = e.getPlayer();
		player.getWorld().setSpawn(new Location(0, 0, 0));
		player.sendMessage(C.GREEN + "World spawn set to (0,0,0)");
	}

}
```

By default, if the executor is not overriden Osmium will display a list of the command arguments to the command sender.

Also, if the command class contains subcommands, execute() will ONLY be called if the command is executed without parameters

### Configurations

Creating a configuration file is as easy as defining a class with the settings that you need. 

```
@Configuration(path = "plugin.conf")
public class Config {

	@Setting
	public static boolean debug = false; //These are default values

	@Setting
	public static String apiKey;

	@Setting
	public static int radius;

}
```

Thats it. There's no need to get involved with files or nodes or anything. Osmium will automatically detect a configuration from the annotation and update the fields accordingly.

Then, to access or modify the config, all you need to do is modify the fields of the config class.

To reload or save the config, use Osmium.reloadConfig(Config.class) or Osmium.saveConfig(Config.class).


### Platform Specific Code

Osmium has many features in place to allow you to use platform specific code, for situations where corresponding functionality hasn't yet been implemented.

For example:

```
Platform.isBukkit();
Platform.isSponge();
```

or

```
Osmum.on(Platform.BUKKIT, () -> {
	Bukkit.getBannedPlayers().forEach(System.out::println);
});
```

To retrieve a value use Osmium.getValue(Callable bukkit, Callable sponge);