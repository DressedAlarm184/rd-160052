/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.rubydung.level;

import com.mojang.rubydung.HitResult;
import com.mojang.rubydung.Player;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.level.Chunk;
import com.mojang.rubydung.level.DirtyChunkSorter;
import com.mojang.rubydung.level.Frustum;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelListener;
import com.mojang.rubydung.level.Tessellator;
import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.phys.AABB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class LevelRenderer
implements LevelListener {
	public static final int MAX_REBUILDS_PER_FRAME = 8;
	public static final int CHUNK_SIZE = 16;
	private Level level;
	private Chunk[] chunks;
	private int xChunks;
	private int yChunks;
	private int zChunks;

	public LevelRenderer(Level level) {
		this.level = level;
		level.addListener(this);
		this.xChunks = level.width / 16;
		this.yChunks = level.depth / 16;
		this.zChunks = level.height / 16;
		this.chunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
		int x = 0;
		while (x < this.xChunks) {
			int y = 0;
			while (y < this.yChunks) {
				int z = 0;
				while (z < this.zChunks) {
					int x0 = x * 16;
					int y0 = y * 16;
					int z0 = z * 16;
					int x1 = (x + 1) * 16;
					int y1 = (y + 1) * 16;
					int z1 = (z + 1) * 16;
					if (x1 > level.width) {
						x1 = level.width;
					}
					if (y1 > level.depth) {
						y1 = level.depth;
					}
					if (z1 > level.height) {
						z1 = level.height;
					}
					this.chunks[(x + y * this.xChunks) * this.zChunks + z] = new Chunk(level, x0, y0, z0, x1, y1, z1);
					++z;
				}
				++y;
			}
			++x;
		}
	}

	public List<Chunk> getAllDirtyChunks() {
		ArrayList<Chunk> dirty = null;
		int i = 0;
		while (i < this.chunks.length) {
			Chunk chunk = this.chunks[i];
			if (chunk.isDirty()) {
				if (dirty == null) {
					dirty = new ArrayList<Chunk>();
				}
				dirty.add(chunk);
			}
			++i;
		}
		return dirty;
	}

	public void render(Player player, int layer) {
		GL11.glEnable(3553);
		int id = Textures.loadTexture("/assets/terrain.png", 9728);
		GL11.glBindTexture(3553, id);
		Frustum frustum = Frustum.getFrustum();

		for (int i = 0; i < this.chunks.length; i++) {
			Chunk chunk = this.chunks[i];

			float dx = (chunk.x0 + chunk.x1) * 0.5f - (float)player.x;
			float dy = (chunk.y0 + chunk.y1) * 0.5f - (float)player.y;
			float dz = (chunk.z0 + chunk.z1) * 0.5f - (float)player.z;
			float distSq = dx * dx + dy * dy + dz * dz;

			if (distSq <= 14400 && frustum.isVisible(chunk.aabb)) {
				chunk.render(layer);
			}
		}

		GL11.glDisable(3553);
	}

	public void updateDirtyChunks(Player player) {
		List<Chunk> dirty = this.getAllDirtyChunks();
		if (dirty == null) {
			return;
		}
		Collections.sort(dirty, new DirtyChunkSorter(player, Frustum.getFrustum()));
		int i = 0;
		while (i < 8 && i < dirty.size()) {
			dirty.get(i).rebuild();
			++i;
		}
	}

	public void pick(Player player) {
		Tessellator t = Tessellator.instance;
		float r = 3.0f;
		AABB box = player.bb.grow(r, r, r);
		int x0 = (int)box.x0;
		int x1 = (int)(box.x1 + 1.0f);
		int y0 = (int)box.y0;
		int y1 = (int)(box.y1 + 1.0f);
		int z0 = (int)box.z0;
		int z1 = (int)(box.z1 + 1.0f);
		GL11.glInitNames();
		int x = x0;
		while (x < x1) {
			GL11.glPushName((int)x);
			int y = y0;
			while (y < y1) {
				GL11.glPushName((int)y);
				int z = z0;
				while (z < z1) {
					GL11.glPushName((int)z);
					Tile tile = Tile.tiles[this.level.getTile(x, y, z)];
					if (tile != null) {
						GL11.glPushName((int)0);
						int i = 0;
						while (i < 6) {
							GL11.glPushName((int)i);
							t.init();
							tile.renderFaceNoTexture(t, x, y, z, i);
							t.flush();
							GL11.glPopName();
							++i;
						}
						GL11.glPopName();
					}
					GL11.glPopName();
					++z;
				}
				GL11.glPopName();
				++y;
			}
			GL11.glPopName();
			++x;
		}
	}

	public void renderHit(HitResult h) {
		float alpha = ((float)Math.sin((double)System.currentTimeMillis() / 200.0) * 0.05f + 0.2f);
		Tessellator t = Tessellator.instance;
		GL11.glEnable((int)3042);
		GL11.glBlendFunc((int)770, (int)1);
		if (h.y == level.depth - 1 && h.f == 1) {
			GL11.glColor4f((float)1.0f, (float)0.0f, (float)0.0f, alpha * 1.5f);
		} else GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, alpha);
		t.init();
		Tile.rock.renderFaceNoTexture(t, h.x, h.y, h.z, h.f);
		t.flush();
		GL11.glDisable((int)3042);
	}

	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 /= 16;
		x1 /= 16;
		y0 /= 16;
		y1 /= 16;
		z0 /= 16;
		z1 /= 16;
		if (x0 < 0) {
			x0 = 0;
		}
		if (y0 < 0) {
			y0 = 0;
		}
		if (z0 < 0) {
			z0 = 0;
		}
		if (x1 >= this.xChunks) {
			x1 = this.xChunks - 1;
		}
		if (y1 >= this.yChunks) {
			y1 = this.yChunks - 1;
		}
		if (z1 >= this.zChunks) {
			z1 = this.zChunks - 1;
		}
		int x = x0;
		while (x <= x1) {
			int y = y0;
			while (y <= y1) {
				int z = z0;
				while (z <= z1) {
					this.chunks[(x + y * this.xChunks) * this.zChunks + z].setDirty();
					++z;
				}
				++y;
			}
			++x;
		}
	}

	@Override
	public void tileChanged(int x, int y, int z) {
		this.setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
	}

	@Override
	public void lightColumnChanged(int x, int z, int y0, int y1) {
		this.setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
	}

	@Override
	public void allChanged() {
		this.setDirty(0, 0, 0, this.level.width, this.level.depth, this.level.height);
	}
}

