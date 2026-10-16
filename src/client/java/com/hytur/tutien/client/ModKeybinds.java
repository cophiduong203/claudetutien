package com.hytur.tutien.client;

import com.hytur.tutien.config.TuTienConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Dot 1 chi co phim bat/tat HUD (mac dinh de trong, doi trong Controls).
 * Cac phim H, I, Z se them o Dot 3 va Dot 7. Phim R khong bao gio bi chiem.
 */
public final class ModKeybinds {
	public static final String CATEGORY = "key.categories.hytur_tu_tien";
	public static KeyBinding toggleHud;

	private ModKeybinds() {}

	public static void register() {
		toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.hytur_tu_tien.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleHud.wasPressed()) {
				TuTienConfig c = TuTienConfig.get();
				c.hudEnabled = !c.hudEnabled;
				TuTienConfig.save();
			}
		});
	}
}
