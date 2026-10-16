package com.hytur.tutien.cultivation;

import com.hytur.tutien.attribute.AttributeScaling;
import com.hytur.tutien.config.TuTienConfig;
import com.hytur.tutien.data.PlayerDataState;
import com.hytur.tutien.effect.BreakthroughEffects;
import com.hytur.tutien.network.ModNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class CultivationManager {
	private CultivationManager() {}

	/** Tu vi can de len tu tang `level` len `level+1`. */
	public static long requiredQi(int level) {
		if (level >= CultivationData.MAX_LEVEL) return 0;
		TuTienConfig c = TuTienConfig.get();
		double v = c.baseQi
				* Math.pow(c.levelGrowth, level - 1)
				* Math.pow(c.realmGrowth, (level - 1) / 10);
		if (v >= 9.0e18) return Long.MAX_VALUE;
		return Math.max(1L, Math.round(v));
	}

	public static CultivationData get(ServerPlayerEntity player) {
		return PlayerDataState.of(player.server).data(player.getUuid());
	}

	/** Cong tu vi va tu dong dot pha (lap lai neu du nhieu tang). Tu vi du duoc giu lai. */
	public static void addQi(ServerPlayerEntity player, long amount) {
		if (amount <= 0) return;
		PlayerDataState state = PlayerDataState.of(player.server);
		CultivationData d = state.data(player.getUuid());
		int old = d.getLevel();
		if (old >= CultivationData.MAX_LEVEL) return;

		long qi;
		try {
			qi = Math.addExact(d.getQi(), amount);
		} catch (ArithmeticException e) {
			qi = Long.MAX_VALUE;
		}
		int lvl = old;
		while (lvl < CultivationData.MAX_LEVEL) {
			long req = requiredQi(lvl);
			if (qi < req) break;
			qi -= req;
			lvl++;
		}
		if (lvl >= CultivationData.MAX_LEVEL) qi = 0;

		d.setLevel(lvl);
		d.setQi(qi);
		state.markDirty();

		if (lvl > old) {
			AttributeScaling.apply(player, lvl);
			player.setHealth(player.getMaxHealth());
			BreakthroughEffects.play(player, old, lvl);
		}
		ModNetworking.sync(player);
	}

	public static void setLevel(ServerPlayerEntity player, int level) {
		PlayerDataState state = PlayerDataState.of(player.server);
		CultivationData d = state.data(player.getUuid());
		d.setLevel(level);
		d.setQi(0);
		state.markDirty();
		AttributeScaling.apply(player, d.getLevel());
		ModNetworking.sync(player);
	}

	public static void reset(ServerPlayerEntity player) {
		setLevel(player, 1);
	}
}
