# Osmium API

Osmium is an abstraction layer for Bukkit and Sponge which aims to provide the *easiest most concise way to create Minecraft plugins period*. Everything in Osmium is done with only one method or one class. There is no registration of commands/listeners/configurations 

In addition to being able to target multiple platforms, Osmium has many unique features which drastically reduce the amount of code necessary to complete many common tasks. Everything from listener/command registration, to config file handling, to database storage is handled completely automatically.

In order to achieve maximum compatibility between platforms, only functionality that can be safely implemented across both platforms will be implemented. Osmium is not intended to be a replacement for Bukkit or Sponge. Osmium is currently in ALPHA so the API is far from complete. However, plugins requiring any functionality that has not yet been implemented can easily write platform specific code as needed. Osmium even has many utility methods to help you do this.

Please join the Discord server if you have questions, would like to stay updated with the project or want to help out: https://discord.gg/jBjYckt

Some servers already using Osmium:
* https://www.voidflame.com

# Features

* Platform independent
* More concise command API and automatic registration of commands
* More concise listener API and automatic registration of listeners
* Service API that allows hooking into common server events at any time
* Super easy configuration handling
* And much more...
 
 
#### Implemented:

* Configs
* Commands
* Listeners
* Schedulers
* Some events

#### TODO:
* More events
* More methods
* Inventories
* Databases
* Everything else...
 
<hr>

# Maven

Osmium can be downloaded from Maven Central.

	<dependency>
    	<groupId>com.kmecpp</groupId>
    	<artifactId>osmium</artifactId>
    	<version>1.0-SNAPSHOT</version>
	</dependency>

 
<hr>

# Examples


### Listeners


Registering listeners is as easy as adding an @Listener annotation to a valid method. There's no need to register the listener or implement any interfaces.

```
@Listener
public void onPlayerJoin(PlayerJoinEvent e){
	//Listener will work without any additional code needed
}
```
**Important Note:** The containing class MUST have a default constructor. If for whatever reason this is not possible, you just have to call plugin.enableEvents(listener) to provide Osmium with an instance of the class.

### Configurations

Creating a configuration file is as easy as defining a class with the settings that you need. 

```
@ConfigProperties(path = "plugin.conf", header="Awsome plugin by kmecpp")
public class Config {

	@Setting(comment = "Enable debug logging")
	public static boolean debug = false; //These are default values

	@Setting(type = Integer.class)
	public static HashSet<Integer> bannedItems;
	
	public static class WebApp {
	
		@Setting
		public static String apiKey;
		
		@Setting
		public static int timeout;
		
	}
	
}
```

Thats it. There's no need to get involved with file handling, config loading or anything. Osmium will automatically detect a configuration from the annotation and update the fields accordingly.

Then, to access or modify the config, all you need to do is modify the fields of the config class.

To reload or save the config, use Osmium.reloadConfig(Config.class) or Osmium.saveConfig(Config.class).

**Performance Comparison (Lower is Better)**

![alt text](https://i.imgur.com/u6qQ4zZ.png)

These results are based off of a very primitive benchmark. If anyone is interested in making these results more accurate please feel free to submit a PR.

### Commands

There are a few ways to create commands.

The first way is the shortest and is useful when you only have a few very simple commands

```
Osmium.registerCommand("spawn", "sp")
	.setDescription("Teleport to the spawn point of your current world")
	.setPermission("myplugin.spawn")
	.setConsole()
	.onExecute((e) -> {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	})
```

Notice the setConsole() method. 

Alternatively you can move the command into its own class. The same command would look like this

```
@CommandProperties(aliases = { "kill", "destroy"}, 
	description = "Teleport to the spawn point of your current world", 
	permission = "myplugin.spawn", console = true)
public class EnjinNewsCommand extends Command {

	@Override
	public void execute(CommandEvent e) {
		World world = e.getWorld(0); //Gets the argument at index 0 and parses it as a World
		world.getPlayers().forEach(Player::kill)
		e.sendMessage(C.GREEN + "You have killed everyone in " + world.getName());
	}

}
```

This single `CommandEvent` parameter system combines the ease of Sponge's object parsing system with the simplicity of Bukkit's API. Instead of being required to define arbitrary key names, all the arguments can be accessed and parsed using their index.

Osmium also has a built in API for creating commands 

Take the /spawn command from the previous examples. Let's expand its functionality so we have the following list of options

Ex:
- /kill - Teleport to the current world's spawn
- /kill all - Sets the spawn point of the current world to (0,0,0)
- /kill <world> - Sets the spawn point of the current world

```
@Command(aliases = { "spawn", "sp"},
	description = "Teleport to the spawn point of your current world",
	permission = "myplugin.spawn")
public class EnjinNewsCommand extends OsmiumCommand {

	@Override
	public void execute(CommandEvent e) {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	}

	@Override
	public void configure(){
		add("set").setAdmin(true).setExecutor(e) -> {
			Player player = e.getPlayer();
			player.getWorld().setSpawn(player.getLocation());
			player.sendMessage(C.GREEN + "New spawn point set successfully!");
		});
		
		add("delete").setAdmin(true).setExecutor(e) -> {
			Player player = e.getPlayer();
			player.getWorld().setSpawn(new Location(0, 0, 0));
			player.sendMessage(C.GREEN + "World spawn set to (0,0,0)");
		});
	}

}
```

By default, if the executor is not overriden Osmium will display a list of the command arguments to the command sender.

Also, if the command class contains subcommands, execute() will ONLY be called if the command is executed without parameters

### Built-In Plugin

Osmium comes with its very own built-in plugin written entirely with the Osmium API.

The plugin allows you do perform a wide range of 

### Persistent Data

Osmium has an extremely easy way to store simple persistent data. Just add @Persistent to a field and it will automatically store its value in the plugin's data file.

The value the field is initialized with will be the default value. It is overwritten once the plugin is loaded.

Things get a little hairy if you change the field name or move it to another class, so it is recommended that you only use this for data that can be deleted on plugin updates.

Example:

	@Persistent
    public static int blocksBroken = -1;


### Databases

TODO

### Platform Specific Code

Osmium has many features in place to allow you to use platform specific code, for situations where corresponding functionality hasn't yet been implemented.

For example:

```
if (Platform.isBukkit()){
	//Bukkit code
} else if(Platform.isSponge()){
	//Sponge code
}
```

or


	Osmum.on(Platform.BUKKIT, () -> {
		Bukkit.getBannedPlayers().forEach(System.out::println);
	});


To retrieve a value use Osmium.getValue(Callable bukkit, Callable sponge);

<hr>

# More Features

### Metrics

Osmium has a built in metrics feature for collecting statistics through <https://bstats.org>. To enable metrics for your plugin simply call the enableMetrics() method in your main plugin class.

**Note:** Metrics in Osmium are disabled by default to comply with Sponge's plugin guidelines. However, server owners will receive a disableable message prompting them to enable metrics if they are disabled. Calling enableMetrics() will only send statistics if the server owners allow it.


	@Override
	public void onInit() {
		enableMetrics();
	}
