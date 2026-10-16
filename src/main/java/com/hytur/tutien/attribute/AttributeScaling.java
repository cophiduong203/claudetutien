package com.hytur.tutien.attribute;

import com.hytur.tutien.config.TuTienConfig;
import com.hytur.tutien.cultivation.CultivationManager;
import com.hytur.tutien.network.ModNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Tang chi so theo tang tu vi bang Entity Attribute Modifier voi UUID co dinh.
 * Moi lan ap dung: go modifier cu theo UUID roi them lai => khong bao gio cong don.
 */
public final class AttributeScaling {
	private static final UUID HEALTH = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000001");
	private static final UUID ATTACK = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000002");
	private static final UUID ARMOR = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000003");
	private static final UUID TOUGH = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000004");
	private static final UUID SPEED = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000005");
	private static final UUID KB = UUID.fromString("7b1f2a10-6a5e-4c11-9a01-3d6e5f000006");

	private AttributeScaling() {}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity p = handler.player;
			apply(p, CultivationManager.get(p).getLevel());
			ModNetworking.sync(p);
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			apply(newPlayer, CultivationManager.get(newPlayer).getLevel());
			if (!alive) newPlayer.setHealth(newPlayer.getMaxHealth());
			ModNetworking.sync(newPlayer);
		});
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			apply(player, CultivationManager.get(player).getLevel());
			ModNetworking.sync(player);
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 != 0) return;
			for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
				if (!p.isAlive() || p.getHealth() >= p.getMaxHealth()) continue;
				float regen = (float) regenPerSecond(CultivationManager.get(p).getLevel());
				if (regen > 0) p.heal(regen);
			}
		});
	}

	// ---- Cong thuc chi so (chi tinh phan thuong them) ----
	private static double realmIndex(int level) {
		return Math.max(0, Math.min(9, (level - 1) / 10));
	}

	private static double n(int level) {
		return Math.max(0, level - 1);
	}

	public static double bonusHealth(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxBonusHealth, n(level) * c.healthPerLevel + realmIndex(level) * c.healthPerRealm) * c.statMultiplier;
	}

	public static double bonusAttack(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxBonusAttack, n(level) * c.attackPerLevel + realmIndex(level) * c.attackPerRealm) * c.statMultiplier;
	}

	public static double bonusArmor(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxBonusArmor, n(level) * c.armorPerLevel + realmIndex(level) * c.armorPerRealm) * c.statMultiplier;
	}

	public static double bonusToughness(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxBonusToughness, n(level) * c.toughnessPerLevel + realmIndex(level) * c.toughnessPerRealm) * c.statMultiplier;
	}

	public static double bonusSpeed(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxBonusSpeed, n(level) * c.speedPerLevel + realmIndex(level) * c.speedPerRealm) * c.statMultiplier;
	}

	public static double knockbackResistance(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxKnockbackRes, n(level) * c.knockbackResPerLevel + realmIndex(level) * c.knockbackResPerRealm) * c.statMultiplier;
	}

	public static double regenPerSecond(int level) {
		TuTienConfig c = TuTienConfig.get();
		return Math.min(c.maxRegenPerSecond, c.regenBase + n(level) * c.regenPerLevel) * c.statMultiplier;
	}

	/** He so suc manh kiem thuat (1.0 o tang 1), dung cho cac ky nang o cac dot sau. */
	public static double swordPower(int level) {
		return 1.0 + n(level) * 0.05 + realmIndex(level) * 0.25;
	}

	public static void apply(ServerPlayerEntity p, int level) {
		set(p, EntityAttributes.GENERIC_MAX_HEALTH, HEALTH, "Hytur Tu Tien health", bonusHealth(level), Operation.ADDITION);
		set(p, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK, "Hytur Tu Tien attack", bonusAttack(level), Operation.ADDITION);
		set(p, EntityAttributes.GENERIC_ARMOR, ARMOR, "Hytur Tu Tien armor", bonusArmor(level), Operation.ADDITION);
		set(p, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, TOUGH, "Hytur Tu Tien toughness", bonusToughness(level), Operation.ADDITION);
		set(p, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED, "Hytur Tu Tien speed", bonusSpeed(level), Operation.MULTIPLY_BASE);
		set(p, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, KB, "Hytur Tu Tien knockback", knockbackResistance(level), Operation.ADDITION);
		if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
	}

	private static void set(ServerPlayerEntity p, EntityAttribute attr, UUID id, String name, double value, Operation op) {
		EntityAttributeInstance inst = p.getAttributeInstance(attr);
		if (inst == null) return;
		inst.removeModifier(id);
		if (value > 0) {
			inst.addPersistentModifier(new EntityAttributeModifier(id, name, value, op));
		}
	}
}
