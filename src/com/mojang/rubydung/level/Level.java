/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.level;

import com.mojang.rubydung.level.LevelListener;
import com.mojang.rubydung.level.PerlinNoiseFilter;
import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.phys.AABB;
import com.mojang.rubydung.Player;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Level {
	private static final int TILE_UPDATE_INTERVAL = 400;
	public final int width;
	public final int height;
	public final int depth;
	public byte[] blocks;
	private int[] lightDepths;
	private ArrayList<LevelListener> levelListeners = new ArrayList();
	public Random random = new Random();
	int unprocessed = 0;

	public Level(int w, int h, int d) {
		this.width = w;
		this.height = h;
		this.depth = d;
		this.blocks = new byte[w * h * d];
		this.lightDepths = new int[w * h];
	}

	public boolean load() {
		try {
			DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(new File("level.dat"))));
			dis.readFully(this.blocks);
			this.calcLightDepths(0, 0, this.width, this.height);
			int i = 0;
			while (i < this.levelListeners.size()) {
				this.levelListeners.get(i).allChanged();
				++i;
			}
			dis.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public void save() {
		try {
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(new File("level.dat"))));
			dos.write(this.blocks);
			dos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calcLightDepths(int x0, int y0, int x1, int y1) {
		int x = x0;
		while (x < x0 + x1) {
			int z = y0;
			while (z < y0 + y1) {
				int oldDepth = this.lightDepths[x + z * this.width];
				int y = this.depth - 1;
				while (y > 0 && !this.isLightBlocker(x, y, z)) {
					--y;
				}
				this.lightDepths[x + z * this.width] = y;
				if (oldDepth != y) {
					int yl0 = oldDepth < y ? oldDepth : y;
					int yl1 = oldDepth > y ? oldDepth : y;
					int i = 0;
					while (i < this.levelListeners.size()) {
						this.levelListeners.get(i).lightColumnChanged(x, z, yl0, yl1);
						++i;
					}
				}
				++z;
			}
			++x;
		}
	}

	public void addListener(LevelListener levelListener) {
		this.levelListeners.add(levelListener);
	}

	public void removeListener(LevelListener levelListener) {
		this.levelListeners.remove(levelListener);
	}

	public boolean isLightBlocker(int x, int y, int z) {
		Tile tile = Tile.tiles[this.getTile(x, y, z)];
		if (tile == null) {
			return false;
		}
		return tile.blocksLight();
	}

	public ArrayList<AABB> getCubes(AABB aABB) {
		ArrayList<AABB> aABBs = new ArrayList<AABB>();
		int x0 = (int)aABB.x0;
		int x1 = (int)(aABB.x1 + 1.0f);
		int y0 = (int)aABB.y0;
		int y1 = (int)(aABB.y1 + 1.0f);
		int z0 = (int)aABB.z0;
		int z1 = (int)(aABB.z1 + 1.0f);
		if (x0 < 0) {
			x0 = 0;
		}
		if (y0 < 0) {
			y0 = 0;
		}
		if (z0 < 0) {
			z0 = 0;
		}
		if (x1 > this.width) {
			x1 = this.width;
		}
		if (y1 > this.depth) {
			y1 = this.depth;
		}
		if (z1 > this.height) {
			z1 = this.height;
		}
		int x = x0;
		while (x < x1) {
			int y = y0;
			while (y < y1) {
				int z = z0;
				while (z < z1) {
					Tile tile = Tile.tiles[this.getTile(x, y, z)];
					if (tile != null) {
						aABBs.add(tile.getAABB(x, y, z));
					}
					++z;
				}
				++y;
			}
			++x;
		}
		return aABBs;
	}

	public boolean setTile(int x, int y, int z, int type) {
		if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
			return false;
		}
		if (type == this.blocks[(y * this.height + z) * this.width + x]) {
			return false;
		}
		this.blocks[(y * this.height + z) * this.width + x] = (byte)type;
		this.calcLightDepths(x, z, 1, 1);
		int i = 0;
		while (i < this.levelListeners.size()) {
			this.levelListeners.get(i).tileChanged(x, y, z);
			++i;
		}
		return true;
	}

	public boolean isLit(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
			return true;
		}
		return y >= this.lightDepths[x + z * this.width];
	}

	public int getTile(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
			return 0;
		}
		return this.blocks[(y * this.height + z) * this.width + x];
	}

	public boolean isSolidTile(int x, int y, int z) {
		Tile tile = Tile.tiles[this.getTile(x, y, z)];
		if (tile == null) {
			return false;
		}
		return tile.isSolid();
	}

	public void tick(Player player) {
		int minX = Math.max(0, (int) player.x - 100);
		int maxX = Math.min(this.width, (int) player.x + 100);

		int minZ = Math.max(0, (int) player.z - 100);
		int maxZ = Math.min(this.height, (int) player.z + 100);

		int rangeX = maxX - minX;
		int rangeZ = maxZ - minZ;

		if (rangeX <= 0 || rangeZ <= 0) return;

		int activeVolume = rangeX * rangeZ * this.depth;
		int ticks = activeVolume / 400;

		for (int i = 0; i < ticks; i++) {
			int x = minX + this.random.nextInt(rangeX);
			int z = minZ + this.random.nextInt(rangeZ);
			int y = this.random.nextInt(this.depth);

			Tile tile = Tile.tiles[this.getTile(x, y, z)];
			if (tile != null) {
				tile.tick(this, x, y, z, this.random);
			}
		}
	}
}

