package com.mojang.rubydung.level;

import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.level.Level;

public class LevelGenerator {
	public static void generateMap(Level level) {
		int w = level.width;
		int h = level.height;
		int d = level.depth;

		int[] heightmap1 = new PerlinNoiseFilter(0).read(w, h);
		int[] heightmap2 = new PerlinNoiseFilter(0).read(w, h);
		int[] cf = new PerlinNoiseFilter(1).read(w, h);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < d; y++) {
				for (int z = 0; z < h; z++) {
					int dh;
					int dh1 = heightmap1[x + z * level.width];
					int dh2 = heightmap2[x + z * level.width];
					int cfh = cf[x + z * level.width];
					if (cfh < 128) dh2 = dh1;

					if (dh2 > (dh = dh1)) {
						dh = dh2;
					} else {
						dh2 = dh1;
					}

					dh = dh / 8 + d / 3;
					int rh = dh - 4;
					int i = (y * level.height + z) * level.width + x;
					int id = 0;
					
					if (y <= 2) {
						id = Tile.bedrock.id;
					} else if (y <= rh) {
						id = Tile.rock.id;
					} else if (y < dh) {
						id = Tile.dirt.id;
					} else if (y == dh) {
						id = Tile.grass.id;
					}
					
					level.blocks[i] = (byte)id;
				}
			}
		}
	}
}
