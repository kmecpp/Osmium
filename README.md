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

