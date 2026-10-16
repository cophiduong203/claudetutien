package com.hytur.tutien.client.hud;

import com.hytur.tutien.client.ClientCultivationState;
import com.hytur.tutien.config.TuTienConfig;
import com.hytur.tutien.cultivation.CultivationData;
import com.hytur.tutien.cultivation.Realm;
import com.hytur.tutien.util.NumberFormat;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** HUD nho gon: canh gioi, tang, tu vi va thanh tien do. */
public final class CultivationHud {
	private static final int BAR_WIDTH = 110;

	private CultivationHud() {}

	public static void register() {
		HudRenderCallback.EVENT.register(CultivationHud::render);
	}

	private static void render(DrawContext ctx, float tickDelta) {
		TuTienConfig cfg = TuTienConfig.get();
		MinecraftClient mc = MinecraftClient.getInstance();
		if (!cfg.hudEnabled || !ClientCultivationState.synced) return;
		if (mc.player == null || mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) return;

		TextRenderer tr = mc.textRenderer;
		int level = ClientCultivationState.level;
		Realm realm = Realm.forLevel(level);
		int rgb = 0xFF000000 | realm.color;
		int x = cfg.hudX;
		int y = cfg.hudY;

		Text title = Text.translatable("hud.hytur_tu_tien.realm", realm.displayName(), Realm.tierOf(level));
		ctx.drawTextWithShadow(tr, title, x, y, rgb);

		boolean max = level >= CultivationData.MAX_LEVEL;
		double progress = max ? 1.0 : (ClientCultivationState.required <= 0 ? 0
				: Math.min(1.0, (double) ClientCultivationState.qi / (double) ClientCultivationState.required));
		int by = y + 11;
		ctx.fill(x - 1, by - 1, x + BAR_WIDTH + 1, by + 5, 0xAA000000);
		ctx.fill(x, by, x + (int) (BAR_WIDTH * progress), by + 4, rgb);

		Text qiText = max
				? Text.translatable("hud.hytur_tu_tien.max")
				: Text.translatable("hud.hytur_tu_tien.qi",
						NumberFormat.compact(ClientCultivationState.qi), NumberFormat.compact(ClientCultivationState.required));
		ctx.drawTextWithShadow(tr, qiText, x, by + 8, 0xFFFFFFFF);
	}
}
