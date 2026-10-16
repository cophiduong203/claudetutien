package com.hytur.tutien.client;

import com.hytur.tutien.client.hud.CultivationHud;
import com.hytur.tutien.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class HyturTuTienClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModKeybinds.register();
		CultivationHud.register();

		ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC, (client, handler, buf, sender) -> {
			int level = buf.readInt();
			long qi = buf.readLong();
			long required = buf.readLong();
			client.execute(() -> {
				ClientCultivationState.level = level;
				ClientCultivationState.qi = qi;
				ClientCultivationState.required = required;
				ClientCultivationState.synced = true;
			});
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientCultivationState.reset());
	}
}
