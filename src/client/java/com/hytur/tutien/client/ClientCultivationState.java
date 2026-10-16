package com.hytur.tutien.client;

/** Trang thai tu vi phia client, cap nhat tu goi dong bo cua server. */
public final class ClientCultivationState {
	public static boolean synced = false;
	public static int level = 1;
	public static long qi = 0;
	public static long required = 0;

	private ClientCultivationState() {}

	public static void reset() {
		synced = false;
		level = 1;
		qi = 0;
		required = 0;
	}
}
