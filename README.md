# Osmium API

Osmium is an abstraction layer for Bukkit and Sponge which aims to provide the *easiest most concise way to create Minecraft plugins period!*.

Say goodbye to registering commands or listeners! Writing code to handle configuration files or databases? NOT ANYMORE! No need to store your plugin instance anywhere. No need to write SQL code. Compile your plugin, and it will work automatically on both Bukkit/Spigot and Sponge servers!

Everything in Osmium is also completely optional. Don't want to use our config or database system. You don't have to! If there is any part of our API that we haven't finished yet, just write platform specific code. Osmium is designed to work with existing codebases and helps you handle platform specific code correctly.

Osmium is perfect for servers using Thermos that eventually might upgrade to Sponge or plugin developers that want to write multiplatform plugins FAST.

Osmium is currently in ALPHA so the API is far from complete. However, plugins requiring any functionality that has not yet been implemented can easily write platform specific code as needed. Osmium even has many utility methods to help you do this.

Please join the Discord server if you have questions, would like to stay updated with the project or want to help out: https://discord.gg/jBjYckt

Some servers already using Osmium:
* https://www.voidflame.com

# Features

* Platform independent
* No need to register commands or listeners
* Built in Hibernate + HikariCP integration
* Resources files like plugin.yml don't exist anymore
* Configuration files are generated automatically from a class file
* Super awesome command API that combines the best of both Bukkit and Sponge
* And much much more...


#### Implemented:

* Configs
* Commands
* Listeners
* Schedulers
* Some events

#### TODO:
* More events
* More methods
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

To see a fully working example of an Osmium plugin, check out the [built in plugin we have here](https://github.com/kmecpp/Osmium/tree/master/src/main/java/com/kmecpp/osmium/core).

You can ignore OsmiumBukkitMain and OsmiumSpongeMain as those will be automatically generated for regular Osmium plugins.

## Listeners


Registering listeners is as easy as adding an `@Listener` annotation to a valid method. **There is no need to register the listener or implement any interfaces.**

```
@Listener
public void onPlayerJoin(PlayerJoinEvent e){
	//Listener will work without any additional code needed
}
```
**Important Note:** The containing class MUST have a default constructor. If for whatever reason you'd prefer not to follow this convention, you just have to call plugin.enableEvents(listener) to provide Osmium with an instance of the class.

## Configurations

Creating a configuration file is as easy as defining a class with the settings that you need. 

```
@ConfigProperties(path = "plugin.conf", header="Awsome plugin by kmecpp")
public class Config {

	@Setting(comment = "Enable debug logging")
	public static boolean debug;

	@Setting(type = Integer.class) //When using a generic class you must specify the type
	public static HashSet<Integer> bannedItems;
	
	@Setting(type = Integer.class) //For maps, use string as the key and specify the value type. Nested types are not supported yet
	public static HashMap<String, Integer> worldBorders;
	
	public static class WebApp {
	
		@Setting
		public static String apiKey;
		
		@Setting
		public static int timeout = 3000; //Three thousand is the default value
		
	}
	
}
```

Thats it. There's no need to get involved with file handling, config loading or anything. Osmium will automatically detect a configuration from the annotation and update the fields accordingly.

Then, to access or modify the config, all you need to do is modify the fields of the config class.

To reload or save the config, use Osmium.reloadConfig(Config.class) or Osmium.saveConfig(Config.class).

**Performance Comparison (Lower is Better)**

![alt text](https://i.imgur.com/u6qQ4zZ.png)

These results are based off of a very primitive benchmark. If anyone is interested in making these results more accurate please feel free to submit a PR.

## Commands

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
public class KillCommand extends Command {

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
- /kill - Displays a list of all kill commands
- /kill all - Kills all non-operator players
- /kill world <world> - Kills all players in a specific world
- /kill player <player> - Kills all players in a specific world

```
@Command(aliases = { "kill", "sp"},
	description = "Teleport to the spawn point of your current world",
	permission = "myplugin.spawn",
	admin = true)
public class KillCommands extends OsmiumCommand {

	@Override
	public void configure(){
		add("all").setExecutor(e) -> {
			Osmium.getOnlinePlayers().stream().filter(p -> !p.isOp()).forEach(Player::kill);
			e.sendMessage(C.GREEN + "You have killed everyone!");
		});
		
		add("world").setExecutor(e) -> {
			World world = e.getWorld(0); //Gets the argument at index 0 and parses it as a World
			world.getPlayers().filter(p -> !p.isOp()).forEach(Player::kill)
			e.sendMessage(C.GREEN + "You have killed everyone in " + world.getName());
		});
		
		add("player").setExecutor(e) -> {
			Player player = e.getPlayer(0); //Gets the argument at index 0 and parses it as a Player
			if(player.getName().equals(
			e.sendMessage(C.GREEN + "You have killed everyone in " + world.getName());
		});
	}

}
```

By default, if the executor is not overriden Osmium will display a list of the command arguments to the command sender.

Also, if the command class contains subcommands, execute() will ONLY be called if the command is executed without parameters

## Built-In Plugin

Osmium comes with its very own built-in plugin written entirely with the Osmium API.

The plugin allows you do perform a wide range of management tasks for any plugin using Osmium as well as Osmium itself. This includes

- Viewing important plugin information such as name, version, author, website and dependencies
- Viewing a list of commands for each plugin and information on each
- Reloading plugins and their configurations

## Persistent Data

Osmium has an extremely easy way to store simple persistent data. Just add @Persistent to a field and it will automatically store its value in the plugin's data file.

The value the field is initialized with will be the default value. It is overwritten once the plugin is loaded.

Things get a little hairy if you change the field name or move it to another class, so it is recommended that you only use this for data that can be deleted on plugin updates.

Example:

	@Persistent
    public static int blocksBroken = -1;


## Databases

TODO

## Platform Specific Code

Osmium has many features in place to allow you to use platform specific code, for situations where corresponding functionality hasn't yet been implemented.

For example:

```
if (Platform.isBukkit()){
	//Bukkit code
} else if(Platform.isSponge()){
	//Sponge code
}
```

This class will only loaded on Bukkit:

```
@Bukkit
public class BukkitListener {
	
	@EventHandler
	public void on(BlockBreakEvent e){
		//Do some fancy Bukkit only stuff
	}
	
}
```

or


	Osmum.on(Platform.BUKKIT, () -> {
		Bukkit.getBannedPlayers().forEach(System.out::println);
	});


To retrieve a value use Osmium.getValue(Callable bukkit, Callable sponge);

<hr>

# More Features

## Metrics

Osmium has a built in metrics feature for collecting statistics through <https://bstats.org>. To enable metrics for your plugin simply call the enableMetrics() method in your main plugin class.

**Note:** Metrics in Osmium are disabled by default to comply with Sponge's plugin guidelines. However, server owners will receive a disableable message prompting them to enable metrics if they are disabled. Calling enableMetrics() will only send statistics if the server owners allow it.


	@Override
	public void onInit() {
		enableMetrics();
	}
