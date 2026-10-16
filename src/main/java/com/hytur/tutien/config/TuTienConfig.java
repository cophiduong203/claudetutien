package com.hytur.tutien.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hytur.tutien.HyturTuTien;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Cau hinh luu tai config/hytur_tu_tien.json. */
public class TuTienConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static TuTienConfig instance = new TuTienConfig();

	// ---- Cong thuc tu vi: round(baseQi * levelGrowth^(L-1) * realmGrowth^floor((L-1)/10)) ----
	public long baseQi = 100;
	public double levelGrowth = 1.12;
	public double realmGrowth = 1.35;

	// ---- Nhan tu vi tu quai ----
	/** Tu vi co ban = mau toi da cua quai * he so nay. */
	public double qiPerMobHealth = 1.0;
	public double bossMultiplier = 10.0;
	/** Quai co mau toi da >= gia tri nay duoc coi la Boss. */
	public double bossHealthThreshold = 100.0;
	/** Phan thuong duoc nhan len theo (yeuCau(tang)/yeuCau(1))^mu de quai van co y nghia o tang cao. */
	public double rewardScalingExponent = 0.8;
	/** Cho phep sinh vat khong phai quai (dong vat) cho tu vi. */
	public boolean rewardPassiveMobs = false;

	// ---- Chong farm ----
	public int antiFarmWindowTicks = 1200;
	public int antiFarmFreeKills = 8;
	public double antiFarmPenaltyPerKill = 0.08;
	public double antiFarmMinMultiplier = 0.15;

	// ---- Chi so theo tang (n = tang-1, r = chi so dai canh gioi 0..9) ----
	/** He so nhan chung cho toan bo chi so. 0 = tat tang chi so. */
	public double statMultiplier = 1.0;
	public double healthPerLevel = 0.6;
	public double healthPerRealm = 4.0;
	public double maxBonusHealth = 100.0;
	public double attackPerLevel = 0.15;
	public double attackPerRealm = 1.0;
	public double maxBonusAttack = 30.0;
	public double armorPerLevel = 0.08;
	public double armorPerRealm = 0.5;
	public double maxBonusArmor = 20.0;
	public double toughnessPerLevel = 0.04;
	public double toughnessPerRealm = 0.25;
	public double maxBonusToughness = 10.0;
	/** Cong them vao toc do di chuyen theo ti le (0.1 = +10%). */
	public double speedPerLevel = 0.0008;
	public double speedPerRealm = 0.01;
	public double maxBonusSpeed = 0.25;
	public double knockbackResPerLevel = 0.005;
	public double knockbackResPerRealm = 0.02;
	public double maxKnockbackRes = 0.75;
	/** Mau hoi moi giay (1.0 = nua tim). */
	public double regenBase = 0.05;
	public double regenPerLevel = 0.02;
	public double maxRegenPerSecond = 3.0;

	// ---- Client ----
	public boolean hudEnabled = true;
	public int hudX = 6;
	public int hudY = 6;
	/** LOW, MEDIUM hoac HIGH (dung cho hieu ung o cac dot sau). */
	public String graphicsQuality = "HIGH";

	public static TuTienConfig get() {
		return instance;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("hytur_tu_tien.json");
	}

	public static void load() {
		Path p = path();
		try {
			if (Files.exists(p)) {
				TuTienConfig loaded = GSON.fromJson(Files.readString(p), TuTienConfig.class);
				if (loaded != null) instance = loaded;
			}
		} catch (Exception e) {
			HyturTuTien.LOGGER.error("Khong doc duoc config, dung mac dinh", e);
			instance = new TuTienConfig();
		}
		save(); // ghi lai de bo sung cac truong moi
	}

	public static void save() {
		try {
			Files.createDirectories(path().getParent());
			Files.writeString(path(), GSON.toJson(instance));
		} catch (IOException e) {
			HyturTuTien.LOGGER.error("Khong ghi duoc config", e);
		}
	}
}
