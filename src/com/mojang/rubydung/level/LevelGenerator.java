package com.mojang.rubydung.level;

import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.level.Level;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class LevelGenerator {
	public static void generateMap(Level level, Random random) {
		int total = level.width * level.height * level.depth;
		int generated = 0;

		int w = level.width;
		int h = level.height;
		int d = level.depth;

		int[] heightmap1 = new PerlinNoiseFilter(0).read(w, h);
		int[] heightmap2 = new PerlinNoiseFilter(0).read(w, h);
		int[] cf = new PerlinNoiseFilter(1).read(w, h);

		JDialog progress_dialog = new JDialog();
		JLabel progress_label = new JLabel(String.format("Generated: 0 / %d (0.0%%)", total));
		progress_dialog.setTitle("Generating level...");
		progress_dialog.add(progress_label);
		progress_dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		progress_dialog.setSize(450, 80);
		progress_label.setFont(progress_label.getFont().deriveFont(20f));
		progress_dialog.setVisible(true);

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
					generated++;
					if (generated % 50000 == 0) {
						progress_label.setText(String.format("\rGenerated: %d / %d (%.1f%%)", generated, total, (double)generated * 100 / (double)total));
					}
				}
			}
		}

		progress_dialog.dispose();

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
		if (x < 0 || z < 0 || x >= l.width || z >= l.height || y < 0 || y >= l.depth) return 0;
		return l.blocks[(y * l.height + z) * l.width + x];
	}

	private static void setTile(Level l, int x, int y, int z, int id) {
		if (x < 0 || z < 0 || x >= l.width || z >= l.height || y < 0 || y >= l.depth) return;
		l.blocks[(y * l.height + z) * l.width + x] = (byte)id;
	}
}

class PerlinNoiseFilter {
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
