package com.hytur.tutien.data;

import com.hytur.tutien.HyturTuTien;
import com.hytur.tutien.cultivation.CultivationData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Du lieu tu tien theo tung nguoi choi, luu trong world (data/hytur_tu_tien.dat).
 * Song sot qua: thoat game, restart server, chet, chuyen dimension.
 */
public class PlayerDataState extends PersistentState {
	private final Map<UUID, CultivationData> players = new HashMap<>();

	public CultivationData data(UUID id) {
		return players.computeIfAbsent(id, k -> new CultivationData());
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtList list = new NbtList();
		for (Map.Entry<UUID, CultivationData> e : players.entrySet()) {
			NbtCompound c = e.getValue().toNbt();
			c.putUuid("Id", e.getKey());
			list.add(c);
		}
		nbt.put("Players", list);
		return nbt;
	}

	public static PlayerDataState fromNbt(NbtCompound nbt) {
		PlayerDataState s = new PlayerDataState();
		NbtList list = nbt.getList("Players", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < list.size(); i++) {
			NbtCompound c = list.getCompound(i);
			if (c.containsUuid("Id")) {
				s.players.put(c.getUuid("Id"), CultivationData.fromNbt(c));
			}
		}
		return s;
	}

	public static PlayerDataState of(MinecraftServer server) {
		PersistentStateManager mgr = server.getOverworld().getPersistentStateManager();
		return mgr.getOrCreate(PlayerDataState::fromNbt, PlayerDataState::new, HyturTuTien.MOD_ID);
	}
}
