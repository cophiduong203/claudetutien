package com.hytur.tutien.network;

import com.hytur.tutien.HyturTuTien;
import com.hytur.tutien.cultivation.CultivationData;
import com.hytur.tutien.cultivation.CultivationManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class ModNetworking {
	/** S2C: int level, long qi, long required. */
	public static final Identifier SYNC = new Identifier(HyturTuTien.MOD_ID, "sync");

	private ModNetworking() {}

	public static void registerServer() {
		// Dot 1 chi co goi S2C; cac goi C2S (ky nang) se them o cac dot sau.
	}

	public static void sync(ServerPlayerEntity player) {
		CultivationData d = CultivationManager.get(player);
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(d.getLevel());
		buf.writeLong(d.getQi());
		buf.writeLong(CultivationManager.requiredQi(d.getLevel()));
		ServerPlayNetworking.send(player, SYNC, buf);
	}
}
