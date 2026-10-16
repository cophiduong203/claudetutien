package com.hytur.tutien.util;

public final class NumberFormat {
	private NumberFormat() {}

	/** 1234 -> 1.23K, 5_000_000 -> 5.00M ... */
	public static String compact(long v) {
		if (v < 1000) return Long.toString(v);
		String[] suffix = {"K", "M", "B", "T", "Qa", "Qi"};
		double d = v;
		int i = -1;
		while (d >= 1000 && i < suffix.length - 1) {
			d /= 1000.0;
			i++;
		}
		return String.format(java.util.Locale.ROOT, "%.2f%s", d, suffix[i]);
	}
}
