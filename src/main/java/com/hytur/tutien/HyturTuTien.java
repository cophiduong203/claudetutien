package com.hytur.tutien;

import com.hytur.tutien.attribute.AttributeScaling;
import com.hytur.tutien.command.TuTienCommands;
import com.hytur.tutien.config.TuTienConfig;
import com.hytur.tutien.cultivation.KillRewardHandler;
import com.hytur.tutien.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyturTuTien implements ModInitializer {
	public static final String MOD_ID = "hytur_tu_tien";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		TuTienConfig.load();
		ModNetworking.registerServer();
		AttributeScaling.register();
		KillRewardHandler.register();
		TuTienCommands.register();
		LOGGER.info("Hytur Tu Tien 3.0 da khoi dong.");
	}
}
