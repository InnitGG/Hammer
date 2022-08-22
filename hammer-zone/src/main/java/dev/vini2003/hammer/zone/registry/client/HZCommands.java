/*
 * MIT License
 *
 * Copyright (c) 2020 - 2022 vini2003
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.vini2003.hammer.zone.registry.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.vini2003.hammer.core.HC;
import dev.vini2003.hammer.core.api.client.color.Color;
import dev.vini2003.hammer.core.api.client.util.InstanceUtil;
import dev.vini2003.hammer.core.api.common.command.argument.ColorArgumentType;
import dev.vini2003.hammer.core.api.common.util.BufUtil;
import dev.vini2003.hammer.zone.api.common.manager.ZoneGroupManager;
import dev.vini2003.hammer.zone.api.common.manager.ZoneManager;
import dev.vini2003.hammer.zone.api.common.zone.Zone;
import dev.vini2003.hammer.zone.registry.common.HZComponents;
import dev.vini2003.hammer.zone.registry.common.HZNetworking;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.text.ClickEvent;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HZCommands {
	private static CompletableFuture<Suggestions> zoneSuggestIds(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
		var client = InstanceUtil.getClient();

		for (var zone : ZoneManager.getAll(client.world)) {
			builder.suggest(zone.getId().toString());
		}
		
		return builder.buildFuture();
	}
	
	private static CompletableFuture<Suggestions> zoneGroupSuggestIds(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
		var client = InstanceUtil.getClient();

		for (var group : ZoneGroupManager.getGroups()) {
			builder.suggest(group.getId().toString());
		}
		
		return builder.buildFuture();
	}
	
	// Enables or disables the zone editor.
	private static int zoneEditor(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		HZValues.ZONE_EDITOR = !HZValues.ZONE_EDITOR;
		
		source.sendFeedback(Text.translatable("command.hammer.zone.editor", HZValues.ZONE_EDITOR ? Text.translatable("text.hammer.enabled.lower_case") : Text.translatable("text.hammer.disabled.lower_case")));
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Selects the zone the player is looking at.
	private static int zoneSelect(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zone = HZValues.getSelectedZone();
		
		if (zone != null) {
			HZValues.setCommandSelectedZone(zone);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.select", zone.getId()));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Selects the zone with the given ID.
	private static int zoneSelectId(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();

		var zoneId = context.getArgument("id", Identifier.class);
		var zone = ZoneManager.get(player.world, zoneId);
		
		if (zone != null) {
			HZValues.setCommandSelectedZone(zone);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.select", zone.getId()));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Deselects the selected zone.
	private static int zoneDeselect(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zone = HZValues.getCommandSelectedZone();
		
		if (zone != null) {
			HZValues.setCommandSelectedZone(null);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.deselect"));
		} else {
		
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Deletes the selected zone.
	private static int zoneDelete(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zone = HZValues.getSelectedZone();
		
		if (zone != null) {
			 var buf = PacketByteBufs.create();
			 buf.writeIdentifier(zone.getId());
			
			ClientPlayNetworking.send(HZNetworking.ZONE_DELETE, buf);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.delete", zone.getId()));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Deletes the zone with the given ID.
	private static int zoneDeleteId(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneId = context.getArgument("id", Identifier.class);
		var zone = ZoneManager.get(player.world, zoneId);
		
		if (zone != null) {
			var buf = PacketByteBufs.create();
			buf.writeIdentifier(zone.getId());
			
			ClientPlayNetworking.send(HZNetworking.ZONE_DELETE, buf);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.delete", zone.getId()));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Creates a zone.
	private static int zoneCreate(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneId = HC.id(UUID.randomUUID().toString().replace("-", ""));
		
		var buf = PacketByteBufs.create();
		buf.writeIdentifier(zoneId);
		
		ClientPlayNetworking.send(HZNetworking.ZONE_CREATE, buf);
		
		source.sendFeedback(Text.translatable("command.hammer.zone.create", zoneId));
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Creates a zone with the given ID.
	private static int zoneCreateId(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneId = context.getArgument("id", Identifier.class);

		var buf = PacketByteBufs.create();
		buf.writeIdentifier(zoneId);
		
		ClientPlayNetworking.send(HZNetworking.ZONE_CREATE, buf);
		
		source.sendFeedback(Text.translatable("command.hammer.zone.create", zoneId));
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Lists the ten first zones in the world.
	private static int zoneList(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var index = 0;
		
		var zones = ZoneManager.getAll(player.world);
		
		if (zones.isEmpty()) {
			source.sendFeedback(Text.translatable("command.hammer.zone.list.none"));
			
			return Command.SINGLE_SUCCESS;
		}
		
		source.sendFeedback(Text.translatable("command.hammer.zone.list.start"));
		
		for (var zone : zones) {
			if (index >= 10) {
				break;
			}
			
			index += 1;
			
			source.sendFeedback(Text.translatable("command.hammer.zone.list.entry", zone.getId(), String.format("#%08X", zone.getColor().toRgba())));
		}
		
		if (zones.size() / 10 > 0) {
			source.sendFeedback(Text.translatable("command.hammer.zone.list.end", 0, zones.size() / 10));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Lists ten of the zones in the world at the given page.
	private static int zoneListPage(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var page = context.getArgument("page", int.class);
		
		var index = 0;
		
		var zones = ZoneManager.getAll(player.world);
		
		if (zones.isEmpty()) {
			source.sendFeedback(Text.translatable("command.hammer.zone.list.none"));
			
			return Command.SINGLE_SUCCESS;
		}
		
		source.sendFeedback(Text.translatable("command.hammer.zone.list"));
		
		for (var zone : zones) {
			if (index > (page * 10) && index < ((page + 1) * 10)) {
				source.sendFeedback(Text.translatable("command.hammer.zone.list.entry", zone.getId(), String.format("#%08X", zone.getColor().toRgba())));
			}
			
			index += 1;
		}
		
		if (zones.size() / 10 > 0) {
			source.sendFeedback(Text.translatable("command.hammer.zone.list.end", page, zones.size() / 10));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Changes the color of the selected zone.
	private static int zoneColor(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneColor = context.getArgument("color", Color.class);
		
		var zone = HZValues.getSelectedZone();
		
		if (zone != null) {
			var buf = PacketByteBufs.create();
			buf.writeIdentifier(zone.getId());
			
			Color.toBuf(zoneColor, buf);
			
			ClientPlayNetworking.send(HZNetworking.ZONE_COLOR_CHANGED, buf);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.color", String.format("#%08X", zone.getColor().toRgba())));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Exports the selected zone.
	private static int zoneExport(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zone = HZValues.getSelectedZone();
		
		if (zone != null) {
			var json = Zone.toJson(zone);
			
			var exportPath = InstanceUtil.getFabric().getGameDir();
			exportPath = exportPath.resolve("export");
			
			if (!Files.exists(exportPath)) {
				try {
					Files.createDirectories(exportPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				var zonePath = exportPath.resolve(zone.getId().toString().replace(":", "-") + ".json");

				var writer = HC.GSON.newJsonWriter(Files.newBufferedWriter(zonePath));
				
				HC.GSON.toJson(json, writer);
				
				writer.close();
				
				source.sendFeedback(Text.translatable("command.hammer.zone.export", zone.getId(), Text.literal(zonePath.getFileName().toString()).formatted(Formatting.UNDERLINE).styled(style -> {
					return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, zonePath.toAbsolutePath().toString()));
				})));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Exports the zone with the given ID.
	private static int zoneExportId(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneId = context.getArgument("id", Identifier.class);
		var zone = ZoneManager.get(player.world, zoneId);
		
		if (zone != null) {
			var json = Zone.toJson(zone);
			
			var exportPath = InstanceUtil.getFabric().getGameDir();
			exportPath = exportPath.resolve("export");
			
			if (!Files.exists(exportPath)) {
				try {
					Files.createDirectories(exportPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				var zonePath = exportPath.resolve(zone.getId().toString().replace(":", "-") + ".json");
				
				var writer = HC.GSON.newJsonWriter(Files.newBufferedWriter(zonePath));
				
				HC.GSON.toJson(json, writer);
				
				writer.close();
				
				source.sendFeedback(Text.translatable("command.hammer.zone.export", zone.getId(), Text.literal(zonePath.getFileName().toString()).formatted(Formatting.UNDERLINE).styled(style -> {
					return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, zonePath.toAbsolutePath().toString()));
				})));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Changes the group of the selected zone.
	private static int zoneGroup(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var zoneGroupId = context.getArgument("id", Identifier.class);

		var zone = HZValues.getSelectedZone();
		
		if (zone != null) {
			var buf = PacketByteBufs.create();
			buf.writeIdentifier(zone.getId());
			buf.writeIdentifier(zoneGroupId);
			
			ClientPlayNetworking.send(HZNetworking.ZONE_GROUP_CHANGED, buf);
			
			source.sendFeedback(Text.translatable("command.hammer.zone.group", zoneGroupId));
		} else {
			source.sendError(Text.translatable("command.hammer.zone.not_found"));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Lists the ten first zone groups in the world.
	private static int zoneGroupList(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();

		var index = 0;
		
		var groups = ZoneGroupManager.getGroups();
		
		if (groups.isEmpty()) {
			source.sendFeedback(Text.translatable("command.hammer.zone.group.list.none"));
			
			return Command.SINGLE_SUCCESS;
		}
		
		source.sendFeedback(Text.translatable("command.hammer.zone.group.list.start"));
		
		for (var group : groups) {
			if (index >= 10) {
				break;
			}
			
			index += 1;
			
			source.sendFeedback(Text.translatable("command.hammer.zone.group.list.entry.outer", group.getId()));
			
			for (var zone : group) {
				source.sendFeedback(Text.translatable("command.hammer.zone.group.list.entry.inner", zone.getId()));
			}
		}
		
		for (var group : groups) {

		}
		
		if (groups.size() / 10 > 0) {
			source.sendFeedback(Text.translatable("command.hammer.zone.group.list.end", 0, groups.size() / 10));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Lists ten of the zone groups in the world at the given page.
	private static int zoneGroupListPage(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var page = context.getArgument("page", int.class);
		
		var index = 0;
		
		var groups = ZoneGroupManager.getGroups();
		
		if (groups.isEmpty()) {
			source.sendFeedback(Text.translatable("command.hammer.zone.group.list.none"));
			
			return Command.SINGLE_SUCCESS;
		}
		
		source.sendFeedback(Text.translatable("command.hammer.zone.group.list.start"));
		
		for (var group : groups) {
			if (index > (page * 10) && index < ((page + 1) * 10)) {
				source.sendFeedback(Text.translatable("command.hammer.zone.group.list.entry.outer", group.getId()));
				
				for (var zone : group) {
					source.sendFeedback(Text.translatable("command.hammer.zone.group.list.entry.inner", zone.getId()));
				}
			}
			
			index += 1;
		}
		
		if (groups.size() / 10 > 0) {
			source.sendFeedback(Text.translatable("command.hammer.zone.group.list.end", 0, groups.size() / 10));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Creates a zone group.
	private static int zoneGroupCreate(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var groupId = context.getArgument("id", Identifier.class);
		
		var buf = PacketByteBufs.create();
		buf.writeIdentifier(groupId);
		
		ClientPlayNetworking.send(HZNetworking.ZONE_GROUP_CREATE, buf);
		
		ZoneGroupManager.getOrCreate(groupId);
		
		source.sendFeedback(Text.translatable("command.hammer.zone.group.create", groupId));
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Deletes a zone group.
	private static int zoneGroupDelete(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var groupId = context.getArgument("id", Identifier.class);
		
		var buf = PacketByteBufs.create();
		buf.writeIdentifier(groupId);
		
		ClientPlayNetworking.send(HZNetworking.ZONE_GROUP_DELETE, buf);
		
		ZoneGroupManager.remove(groupId);
		
		source.sendFeedback(Text.translatable("command.hammer.zone.group.delete", groupId));
		
		return Command.SINGLE_SUCCESS;
	}
	
	// Exports a zone group.
	private static int zoneGroupExport(CommandContext<FabricClientCommandSource> context) {
		var source = context.getSource();
		var player = source.getPlayer();
		
		var groupId = context.getArgument("id", Identifier.class);
		
		var group = ZoneGroupManager.getOrCreate(groupId);
		
		var exportPath = InstanceUtil.getFabric().getGameDir();
		exportPath = exportPath.resolve("export");
		
		if (!Files.exists(exportPath)) {
			try {
				Files.createDirectories(exportPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (var zone : group) {
			var json = Zone.toJson(zone);
			
			try {
				var zoneGroupPath = exportPath.resolve(zone.getId().toString().replace(":", "-") + ".json");
				
				var writer = HC.GSON.newJsonWriter(Files.newBufferedWriter(zoneGroupPath));
				
				HC.GSON.toJson(json, writer);
				
				writer.close();
				
				source.sendFeedback(Text.translatable("command.hammer.zone.group.export", zone.getId(), Text.literal(zoneGroupPath.getFileName().toString()).formatted(Formatting.UNDERLINE).styled(style -> {
					return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, zoneGroupPath.toAbsolutePath().toString()));
				})));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		source.sendFeedback(Text.translatable("command.hammer.zone.group.export", groupId));
		
		return Command.SINGLE_SUCCESS;
	}
	
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					literal("zone").requires(source -> {
						return source.hasPermissionLevel(4);
					}).then(
							literal("editor").executes(HZCommands::zoneEditor)
					).then(
							literal("select").executes(HZCommands::zoneSelect).then(
									argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneSuggestIds).executes(HZCommands::zoneSelectId)
							)
					).then(
							literal("deselect").executes(HZCommands::zoneDeselect)
					).then(
							literal("create").executes(HZCommands::zoneCreate).then(
									argument("id", IdentifierArgumentType.identifier()).executes(HZCommands::zoneCreateId)
							)
					).then(
							literal("delete").executes(HZCommands::zoneDelete).then(
									argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneSuggestIds).executes(HZCommands::zoneDeleteId)
							)
					).then(
							literal("list").executes(HZCommands::zoneList).then(
									argument("page", IntegerArgumentType.integer()).executes(HZCommands::zoneListPage)
							)
					).then(
							literal("color").then(
									literal("set").then(
											argument("color", ColorArgumentType.color()).executes(HZCommands::zoneColor)
									)
							)
					).then(
							literal("export").executes(HZCommands::zoneExport).then(
									argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneSuggestIds).executes(HZCommands::zoneExportId)
							)
					).then(
							literal("group").then(
									literal("set").then(
											argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneGroupSuggestIds).executes(HZCommands::zoneGroup)
									)
							).then(
									literal("list").executes(HZCommands::zoneGroupList).then(
											argument("page", IntegerArgumentType.integer()).executes(HZCommands::zoneGroupListPage)
									)
							).then(
									literal("delete").then(
											argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneGroupSuggestIds).executes(HZCommands::zoneGroupDelete)
									)
							).then(
									literal("create").then(
											argument("id", IdentifierArgumentType.identifier()).executes(HZCommands::zoneGroupCreate)
									)
							).then(
									literal("export").then(
											argument("id", IdentifierArgumentType.identifier()).suggests(HZCommands::zoneGroupSuggestIds).executes(HZCommands::zoneGroupExport)
									)
							)
					)
			);
		});
	}
}
