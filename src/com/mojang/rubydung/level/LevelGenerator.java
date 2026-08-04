package com.mojang.rubydung.level;

import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.level.Level;
import java.util.Random;

public class LevelGenerator {
	public static void generateMap(Level level, Random random) {
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
					
					setTile(level, x, y, z, id);
				}
			}
		}

		int tree_count = (int)Math.floor((w * h) / 500);
		int[][] tree_positions = new int[tree_count][3];

		for (int c = 0; c < tree_count; c++) {
			int x = random.nextInt(w);
			int z = random.nextInt(h);
			int y = d - 1;

			while (true) {
				if (getTile(level, x, y, z) != 0) {
					break;
				} else {
					y--;
				}
			}

			tree_positions[c] = new int[] {x, y, z};
		}

		for (int c = 0; c < tree_count; c++) {
			placeTree(level, tree_positions[c][0], tree_positions[c][1], tree_positions[c][2], random);
		}
	}

	private static void placeTree(Level l, int x, int y, int z, Random random) {
		setTile(l, x, y++, z, Tile.dirt.id);
		int tree_height = random.nextInt(3) + 5;

		for (int i = 0; i < tree_height; i++) {
			setTile(l, x, y++, z, Tile.log.id);
		}

		y -= 2;

		for (int k = 0; k < 2; k++) {
			int sx = x - 2;
			int sz = z - 2;
			for (int j = 0; j < 5; j++) {
				for (int i = 0; i < 5; i++) {
					if ((i == 2 && j == 2) || (i == 0 && j == 4) || (i == 0 && j == 0)
						|| (i == 4 && j == 0) || (i == 4 && j == 4)) continue;
					setTile(l, sx + i, y, sz + j, Tile.leaves.id);
				}
			}
			y++;
		}

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				setTile(l, x - 1 + i, y, z - 1 + j, Tile.leaves.id);
			}
		}

		setTile(l, x, ++y, z, Tile.leaves.id);
		setTile(l, x - 1, y, z, Tile.leaves.id);
		setTile(l, x, y, z + 1, Tile.leaves.id);
		setTile(l, x + 1, y, z, Tile.leaves.id);
		setTile(l, x, y, z - 1, Tile.leaves.id);
	}

	private static byte getTile(Level l, int x, int y, int z) {
		return l.blocks[(y * l.height + z) * l.width + x];
	}

	private static void setTile(Level l, int x, int y, int z, int id) {
		if (x < 0 || z < 0 || x > l.width || z > l.height || y < 0 || y > l.depth) return;
		l.blocks[(y * l.height + z) * l.width + x] = (byte)id;
	}
}
