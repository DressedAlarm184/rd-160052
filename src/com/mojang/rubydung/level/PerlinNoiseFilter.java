/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.level;

import java.util.Random;

public class PerlinNoiseFilter {
	Random random = new Random();
	int seed = this.random.nextInt();
	int levels = 0;
	int fuzz = 16;

	public PerlinNoiseFilter(int levels) {
		this.levels = levels;
	}

	public int[] read(int width, int height) {
		int size = Math.max(width, height);
		int x;
		Random random = new Random();
		int[] tmp = new int[size * size];
		int level = this.levels;
		int step = size >> level;
		int y = 0;
		while (y < size) {
			x = 0;
			while (x < size) {
				tmp[x + y * size] = (random.nextInt(256) - 128) * this.fuzz;
				x += step;
			}
			y += step;
		}
		step = size >> level;
		while (step > 1) {
			int x2;
			int val = 256 * (step << level);
			int ss = step / 2;
			int y2 = 0;
			while (y2 < size) {
				x2 = 0;
				while (x2 < size) {
					int m;
					int ul = tmp[(x2 + 0) % size + (y2 + 0) % size * size];
					int ur = tmp[(x2 + step) % size + (y2 + 0) % size * size];
					int dl = tmp[(x2 + 0) % size + (y2 + step) % size * size];
					int dr = tmp[(x2 + step) % size + (y2 + step) % size * size];
					tmp[x2 + ss + (y2 + ss) * size] = m = (ul + dl + ur + dr) / 4 + random.nextInt(val * 2) - val;
					x2 += step;
				}
				y2 += step;
			}
			y2 = 0;
			while (y2 < size) {
				x2 = 0;
				while (x2 < size) {
					int c = tmp[x2 + y2 * size];
					int r = tmp[(x2 + step) % size + y2 * size];
					int d = tmp[x2 + (y2 + step) % size * size];
					int mu = tmp[(x2 + ss & size - 1) + (y2 + ss - step & size - 1) * size];
					int ml = tmp[(x2 + ss - step & size - 1) + (y2 + ss & size - 1) * size];
					int m = tmp[(x2 + ss) % size + (y2 + ss) % size * size];
					int u = (c + r + m + mu) / 4 + random.nextInt(val * 2) - val;
					int l = (c + d + m + ml) / 4 + random.nextInt(val * 2) - val;
					tmp[x2 + ss + y2 * size] = u;
					tmp[x2 + (y2 + ss) * size] = l;
					x2 += step;
				}
				y2 += step;
			}
			step /= 2;
		}
		int[] result = new int[width * height];
		y = 0;
		while (y < height) {
			x = 0;
			while (x < width) {
				result[x + y * width] = tmp[x % size + y % size * size] / 512 + 128;
				++x;
			}
			++y;
		}
		return result;
	}
}

