package com.hytur.tutien.cultivation;

import net.minecraft.text.Text;

/** 10 dai canh gioi, moi canh gioi 10 tang. */
public enum Realm {
	LUYEN_KHI("luyen_khi", 0xCFEFFF),
	TRUC_CO("truc_co", 0x3DDC6B),
	KIM_DAN("kim_dan", 0xFFC940),
	NGUYEN_ANH("nguyen_anh", 0x3A8BFF),
	HOA_THAN("hoa_than", 0xA64DFF),
	LUYEN_HU("luyen_hu", 0xC2287E),
	HOP_THE("hop_the", 0xFF5A2B),
	DAI_THUA("dai_thua", 0xFFF3B0),
	DO_KIEP("do_kiep", 0x33F0FF),
	CHAN_TIEN("chan_tien", 0xF2F6F7);

	public static final String MOD_ID = "hytur_tu_tien";

	public final String id;
	/** Mau linh khi RGB. */
	public final int color;

	Realm(String id, int color) {
		this.id = id;
		this.color = color;
	}

	public Text displayName() {
		return Text.translatable("realm." + MOD_ID + "." + id);
	}

	public static Realm forLevel(int level) {
		int idx = Math.max(0, Math.min(9, (level - 1) / 10));
		return values()[idx];
	}

	/** Tang trong canh gioi: 1..10. */
	public static int tierOf(int level) {
		return Math.floorMod(Math.max(1, level) - 1, 10) + 1;
	}
}
