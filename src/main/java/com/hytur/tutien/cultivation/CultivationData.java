package com.hytur.tutien.cultivation;

import net.minecraft.nbt.NbtCompound;

public class CultivationData {
	public static final int MAX_LEVEL = 100;

	private int level = 1;
	/** Tu vi da tich luy trong tang hien tai (tu vi du duoc giu lai). */
	private long qi = 0;

	public int getLevel() {
		return level;
	}

	public long getQi() {
		return qi;
	}

	public void setLevel(int level) {
		this.level = Math.max(1, Math.min(MAX_LEVEL, level));
	}

	public void setQi(long qi) {
		this.qi = Math.max(0, qi);
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("Level", level);
		nbt.putLong("Qi", qi);
		return nbt;
	}

	public static CultivationData fromNbt(NbtCompound nbt) {
		CultivationData d = new CultivationData();
		d.setLevel(nbt.contains("Level") ? nbt.getInt("Level") : 1);
		d.setQi(nbt.getLong("Qi"));
		return d;
	}
}
