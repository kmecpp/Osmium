package com.kmecpp.osmium.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.kmecpp.jlib.Validate;

public abstract class OsmiumCommand implements CommandExecutor {

	private String[] aliases;

	private Map<List<String>, CommandSpec> children = new HashMap<List<String>, CommandSpec>();
	private String permission;
	private Text description;

	//	public SpongeCommand(String alias) {
	//		this(new String[] { alias });
	//	}

	public OsmiumCommand(String alias, String... aliases) {
		Validate.notNull(alias);
		this.aliases = new String[aliases.length + 1];
		this.aliases[0] = alias;
		System.arraycopy(aliases, 0, this.aliases, 1, aliases.length);

		//		Stream.of(this.getClass().getMethods())
		//				.filter(method -> method.isAnnotationPresent(Child.class))
		//				.forEach(method -> {
		//					Child child = method.getAnnotation(Child.class);
		//					if (method.getReturnType().equals(CommandSpec.class)) {
		//						if (method.getParameters().length == 0) {
		//							try {
		//								children.put(new ArrayList<String>(Arrays.asList(child.aliases())), (CommandSpec) method.invoke(this));
		//							} catch (InvocationTargetException | IllegalAccessException e) {
		//								childError(child, "Failed to build!");
		//								e.printStackTrace();
		//							}
		//						} else {
		//							childError(child, "Method must be parameterless!");
		//						}
		//					} else {
		//						childError(child, "Return type must be CommandResult!");
		//					}
		//				});
	}

	//	public void childError(Child child, String message) {
	//		SpongeCore.getLogger().error("Invalid child with aliases {" + String.join(", ", child.aliases()) + "}!" + message);
	//	}

	public void registerArg(String aliases, CommandSpec spec) {
		children.put(new ArrayList<String>(Arrays.asList(aliases)), spec);
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setDescription(Text description) {
		this.description = description;
	}

	public String[] getAliases() {
		return aliases;
	}

	public CommandElement getArguments() {
		return GenericArguments.none();
	}

	public ChildList getChildList() {
		return new ChildList(
				Text.of(TextColors.AQUA, TextStyles.BOLD, "Command List"),
				TextColors.YELLOW,
				TextColors.AQUA,
				TextColors.GREEN);
	}

	public abstract CommandResult execute(CommandSource src, CommandContext args);

	public final CommandResult onSpongeCommmand(CommandSource src, CommandContext args) throws CommandException {
		CommandResult result = execute(src, args);
		if (result == Result.SUCCESS) {
			//Success
		} else if (result == Result.CHILD_LIST) {
			ChildList list = getChildList();

			String shortestParentAliases = shortestAlias(getAliases());
			ArrayList<Text> lines = new ArrayList<Text>();
			children.entrySet().stream().forEach((entry) -> {
				lines.add(Text.of(
						list.getCommandColor(), "/" + shortestParentAliases + " " + shortestAlias(entry.getKey().toArray(new String[0])) + " ",
						list.getDescriptionColor(), entry.getValue().getShortDescription(src).orElse(Text.of("No description"))));
			});
			//			for (Entry<List<String>, CommandSpec> entry : children.entrySet()) {
			//				lines.add(Text.of(
			//						list.getCommandColor(), "/" + shortestParentAliases + " " + shortestAlias(entry.getKey().toArray(new String[0])) + " ",
			//						list.getDescriptionColor(), entry.getValue().getShortDescription(src).orElse(Text.of("No description"))));
			//			}

			PaginationList.Builder page = Sponge.getServiceManager().provide(PaginationService.class).get().builder();
			page.title(list.getTitle());
			page.linesPerPage(10);
			page.contents(lines);
			page.padding(Text.of(TextColors.YELLOW, TextStyles.STRIKETHROUGH, "-"));
			page.sendTo(src);

			//			src.sendMessage(Text.EMPTY);
			//			src.sendMessage(list.getTitle());
			//			src.sendMessage(Text.of(list.getDividerColor(), TextStyles.BOLD, TextStyles.STRIKETHROUGH, "------------------------------"));
			//			src.sendMessage(Text.EMPTY);
			//			for (Entry<List<String>, CommandSpec> entry : children.entrySet()) {
			//				src.sendMessage(Text.of(
			//						list.getCommandColor(), "/" + entry.getKey().stream().sorted((s1, s2) -> s1.length() - s2.length()).findFirst().get(),
			//						list.getDescriptionColor(), entry.getValue().getShortDescription(src).orElseGet(Text.of("No description"))));
			//			}
		} else if (result == Result.PERMISSION) {
			throw new CommandException(Text.of(TextColors.RED, "You do not have permission to perform this command!"));
		} else if (result == Result.USAGE) {
			throw new CommandException(Text.of(TextColors.RED, "Incorrect usage!"), true);
		}
		return result;
	}

	public final CommandSpec getSpec() {
		return CommandSpec.builder()
				.children(children)
				.permission(permission)
				.description(description)
				.arguments(getArguments())
				.executor((src, args) -> onSpongeCommmand(src, args))
				.build();
	}

	private static String shortestAlias(String[] aliases) {
		return Arrays.stream(aliases)
				.sorted((s1, s2) -> s1.length() - s2.length())
				.findFirst()
				.get();
	}

}
