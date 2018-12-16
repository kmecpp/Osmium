# Osmium API

Osmium is an abstraction layer for Bukkit and Sponge which aims to provide the *easiest most concise way to create Minecraft plugins period*. Everything in Osmium is done with only one method or one class. There is no registration of commands/listeners/configurations 

In addition to being able to target multiple platforms, Osmium has many unique features which drastically reduce the amount of code necessary to complete many common tasks. Everything from listener/command registration, to config file handling, to database storage is handled completely automatically. Write code to describe functionality, not its implementation.

Although it will take a very long time to implement enough features to have a complete Minecraft API, Osmium should be in a usable state fairly soon. Plugins requiring any part of a specific platform which Osmium has not yet implemented can easily write platform specific code as needed. Osmium even has many utility methods to help you do this.


Join our Discord channel stay updated with the project or help out: https://discord.gg/jBjYckt

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

### Commands

There are a few ways to create commands.

The first way is the shortest and is useful when you only have a few very simple commands

```
Osmium.registerCommand("spawn", "sp")
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
@CommandProperties(aliases = { "spawn", "sp"}, 
	description = "Teleport to the spawn point of your current world", 
	permission = "myplugin.spawn", 
	playersOnly = true)
public class EnjinNewsCommand extends Command {

	@Override
	public void execute(CommandEvent e) {
		Player player = e.getPlayer();
		player.teleport(player.getWorld().getSpawn());
		player.sendMessage(C.GREEN + "You have been teleported to this world's spawn point!");
	}

}
```

This single `CommandEvent` parameter system combines the ease of Sponge's object parsing system with the simplicity of Bukkit's API. Instead of being required to define arbitrary key names, all the arguments can be accessed and parsed using their index.

The last way to define a command is by using it as a parent for subcommands.

Take the /spawn command from the previous examples. If we expand its functionality so we have a list of options

Ex:
- /spawn - Teleport to the current world's spawn
- /spawn set - Sets the spawn point of the current world
- /spawn delete - Sets the spawn point of the current world to (0,0,0)

```
@Command(aliases = { "spawn", "sp"},
	description = "Teleport to the spawn point of your current world",
	permission = "myplugin.spawn",
	playersOnly = true)
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

**Performance Comparison**

![alt text](https://imgur.com/2lESMb6.png)

These results are based off of a very primitive benchmark. If anyone is interested in making these results more accurate please feel free to submit a issue.

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
if(Platform.isBukkit()){
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
