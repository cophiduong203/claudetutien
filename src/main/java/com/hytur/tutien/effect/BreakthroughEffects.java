package com.hytur.tutien.effect;

import com.hytur.tutien.cultivation.Realm;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.joml.Vector3f;

/**
 * Hieu ung dot pha co ban (Dot 1): thong bao, am thanh, vong hat theo mau canh gioi.
 * Hieu ung dien anh day du (bay 10 block, set, rong linh khi) se lam o Dot 4.
 */
public final class BreakthroughEffects {
	private BreakthroughEffects() {}

	public static void play(ServerPlayerEntity player, int oldLevel, int newLevel) {
		Realm realm = Realm.forLevel(newLevel);
		boolean newRealm = Realm.forLevel(oldLevel) != realm;
		int tier = Realm.tierOf(newLevel);

		Text title = Text.translatable("title.hytur_tu_tien.breakthrough")
				.setStyle(Style.EMPTY.withColor(realm.color).withBold(true));
		Text subtitle = Text.translatable("subtitle.hytur_tu_tien.breakthrough", realm.displayName(), tier)
				.setStyle(Style.EMPTY.withColor(realm.color));
		player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 60, 20));
		player.networkHandler.sendPacket(new TitleS2CPacket(title));
		player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));

		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 0.8f);
		if (newRealm) {
			world.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.5f, 1.0f);
		}

		float r = ((realm.color >> 16) & 0xFF) / 255f;
		float g = ((realm.color >> 8) & 0xFF) / 255f;
		float b = (realm.color & 0xFF) / 255f;
		DustParticleEffect dust = new DustParticleEffect(new Vector3f(r, g, b), newRealm ? 2.0f : 1.2f);
		int points = newRealm ? 48 : 24;
		double radius = newRealm ? 2.5 : 1.5;
		for (int i = 0; i < points; i++) {
			double a = 2 * Math.PI * i / points;
			double x = player.getX() + Math.cos(a) * radius;
			double z = player.getZ() + Math.sin(a) * radius;
			world.spawnParticles(dust, x, player.getY() + 0.1, z, 1, 0, 0.05, 0, 0);
			world.spawnParticles(ParticleTypes.END_ROD, x, player.getY() + 0.1, z, 1, 0, 0.4, 0, 0.02);
		}
	}
}
