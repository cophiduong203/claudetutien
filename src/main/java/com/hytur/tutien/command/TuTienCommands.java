package com.hytur.tutien.command;

import com.hytur.tutien.cultivation.CultivationData;
import com.hytur.tutien.cultivation.CultivationManager;
import com.hytur.tutien.cultivation.Realm;
import com.hytur.tutien.effect.BreakthroughEffects;
import com.hytur.tutien.util.NumberFormat;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TuTienCommands {
	private TuTienCommands() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("canhgioi")
						.executes(TuTienCommands::status)
						.then(literal("help").executes(TuTienCommands::help))
						.then(literal("settang")
								.requires(s -> s.hasPermissionLevel(2))
								.then(argument("tang", IntegerArgumentType.integer(1, CultivationData.MAX_LEVEL))
										.executes(TuTienCommands::setLevel)))
						.then(literal("addqi")
								.requires(s -> s.hasPermissionLevel(2))
								.then(argument("amount", LongArgumentType.longArg(1))
										.executes(TuTienCommands::addQi)))
						.then(literal("reset")
								.requires(s -> s.hasPermissionLevel(2))
								.executes(TuTienCommands::reset))
						.then(literal("test")
								.requires(s -> s.hasPermissionLevel(2))
								.then(literal("breakthrough").executes(TuTienCommands::testBreakthrough)))));
	}

	private static int status(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		CultivationData d = CultivationManager.get(p);
		Realm realm = Realm.forLevel(d.getLevel());
		Text msg;
		if (d.getLevel() >= CultivationData.MAX_LEVEL) {
			msg = Text.translatable("command.hytur_tu_tien.status_max", realm.displayName(), Realm.tierOf(d.getLevel()), d.getLevel());
		} else {
			msg = Text.translatable("command.hytur_tu_tien.status", realm.displayName(), Realm.tierOf(d.getLevel()), d.getLevel(),
					NumberFormat.compact(d.getQi()), NumberFormat.compact(CultivationManager.requiredQi(d.getLevel())));
		}
		ctx.getSource().sendFeedback(() -> msg, false);
		return 1;
	}

	private static int help(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendFeedback(() -> Text.translatable("command.hytur_tu_tien.help"), false);
		return 1;
	}

	private static int setLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		int lvl = IntegerArgumentType.getInteger(ctx, "tang");
		CultivationManager.setLevel(p, lvl);
		ctx.getSource().sendFeedback(() -> Text.translatable("command.hytur_tu_tien.settang", lvl), true);
		return 1;
	}

	private static int addQi(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		long amount = LongArgumentType.getLong(ctx, "amount");
		CultivationManager.addQi(p, amount);
		ctx.getSource().sendFeedback(() -> Text.translatable("command.hytur_tu_tien.addqi", NumberFormat.compact(amount)), true);
		return 1;
	}

	private static int reset(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		CultivationManager.reset(p);
		ctx.getSource().sendFeedback(() -> Text.translatable("command.hytur_tu_tien.reset"), true);
		return 1;
	}

	private static int testBreakthrough(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		int lvl = CultivationManager.get(p).getLevel();
		BreakthroughEffects.play(p, Math.max(1, lvl - 1), lvl);
		return 1;
	}
}
