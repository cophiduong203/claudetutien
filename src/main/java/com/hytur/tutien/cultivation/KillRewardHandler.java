package com.hytur.tutien.cultivation;

import com.hytur.tutien.config.TuTienConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Cong tu vi khi diet quai/Boss, co chong farm va chong nhan trung mot su kien. */
public final class KillRewardHandler {
	private static final Map<UUID, Deque<Long>> RECENT_KILLS = new HashMap<>();
	private static final Set<UUID> REWARDED = new LinkedHashSet<>();
	private static final int REWARDED_CAP = 512;

	private KillRewardHandler() {}

	public static void register() {
		ServerLivingEntityEvents.AFTER_DEATH.register(KillRewardHandler::onDeath);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> RECENT_KILLS.remove(handler.player.getUuid()));
	}

	private static void onDeath(LivingEntity victim, DamageSource source) {
		if (victim.getWorld().isClient()) return;
		if (victim instanceof PlayerEntity) return;
		Entity attacker = source.getAttacker();
		if (!(attacker instanceof ServerPlayerEntity player)) return;

		TuTienConfig cfg = TuTienConfig.get();
		double maxHp = victim.getMaxHealth();
		boolean boss = maxHp >= cfg.bossHealthThreshold;
		if (!boss && !(victim instanceof Monster) && !cfg.rewardPassiveMobs) return;

		// Moi thuc the chi duoc thuong mot lan.
		if (!REWARDED.add(victim.getUuid())) return;
		if (REWARDED.size() > REWARDED_CAP) {
			Iterator<UUID> it = REWARDED.iterator();
			it.next();
			it.remove();
		}

		CultivationData d = CultivationManager.get(player);
		if (d.getLevel() >= CultivationData.MAX_LEVEL) return;

		double base = maxHp * cfg.qiPerMobHealth * (boss ? cfg.bossMultiplier : 1.0);
		double ratio = (double) CultivationManager.requiredQi(d.getLevel()) / (double) CultivationManager.requiredQi(1);
		double scale = Math.pow(Math.max(1.0, ratio), cfg.rewardScalingExponent);
		double reward = base * scale * antiFarmMultiplier(player, cfg);

		long amount = Math.max(1L, Math.round(reward));
		CultivationManager.addQi(player, amount);
		player.sendMessage(Text.translatable("message.hytur_tu_tien.gain_qi", amount), true);
	}

	private static double antiFarmMultiplier(ServerPlayerEntity player, TuTienConfig cfg) {
		long now = player.server.getTicks();
		Deque<Long> q = RECENT_KILLS.computeIfAbsent(player.getUuid(), k -> new ArrayDeque<>());
		while (!q.isEmpty() && now - q.peekFirst() > cfg.antiFarmWindowTicks) q.pollFirst();
		int n = q.size();
		q.addLast(now);
		if (n <= cfg.antiFarmFreeKills) return 1.0;
		return Math.max(cfg.antiFarmMinMultiplier, 1.0 - (n - cfg.antiFarmFreeKills) * cfg.antiFarmPenaltyPerKill);
	}
}
