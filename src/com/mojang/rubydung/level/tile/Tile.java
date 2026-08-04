/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tesselator;
import com.mojang.rubydung.level.tile.GrassTile;
import com.mojang.rubydung.particle.Particle;
import com.mojang.rubydung.particle.ParticleEngine;
import com.mojang.rubydung.phys.AABB;
import java.util.Random;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile empty = null;
	public static final Tile rock = new Tile(1, 1);
	public static final Tile grass = new GrassTile(2);
	public static final Tile dirt = new Tile(3, 2);
	public static final Tile cobble = new Tile(4, 16);
	public static final Tile wood = new Tile(5, 4);
	public static final Tile bricks = new Tile(6, 5);
	public static final Tile bedrock = new UnbreakableTile(7, 6);
	public static final Tile log = new MultiTile(8, 8, 8, 7, 7, 7, 7);
	public static final Tile leaves = new TransparentTile(9, 9);

	public int tex;
	public final int id;

	protected Tile(int id) {
		Tile.tiles[id] = this;
		this.id = id;
	}

	protected Tile(int id, int tex) {
		this(id);
		this.tex = tex;
	}

	public void render(Tesselator t, Level level, int layer, int x, int y, int z) {
		float c1 = 1.0f;
		float c2 = 0.8f;
		float c3 = 0.6f;
		if (this.shouldRenderFace(level, x, y - 1, z, layer)) {
			t.color(c1, c1, c1);
			this.renderFace(t, x, y, z, 0);
		}
		if (this.shouldRenderFace(level, x, y + 1, z, layer)) {
			t.color(c1, c1, c1);
			this.renderFace(t, x, y, z, 1);
		}
		if (this.shouldRenderFace(level, x, y, z - 1, layer)) {
			t.color(c2, c2, c2);
			this.renderFace(t, x, y, z, 2);
		}
		if (this.shouldRenderFace(level, x, y, z + 1, layer)) {
			t.color(c2, c2, c2);
			this.renderFace(t, x, y, z, 3);
		}
		if (this.shouldRenderFace(level, x - 1, y, z, layer)) {
			t.color(c3, c3, c3);
			this.renderFace(t, x, y, z, 4);
		}
		if (this.shouldRenderFace(level, x + 1, y, z, layer)) {
			t.color(c3, c3, c3);
			this.renderFace(t, x, y, z, 5);
		}
	}

	private boolean shouldRenderFace(Level level, int x, int y, int z, int layer) {
		return !level.isSolidTile(x, y, z) && level.isLit(x, y, z) ^ layer == 1;
	}

	protected int getTexture(int face) {
		return this.tex;
	}

	public void renderFace(Tesselator t, int x, int y, int z, int face) {
		int tex = this.getTexture(face);
		float u0 = (float)(tex % 16) / 16.0f;
		float u1 = u0 + 0.0624375f;
		float v0 = (float)(tex / 16) / 16.0f;
		float v1 = v0 + 0.0624375f;
		float x0 = (float)x + 0.0f;
		float x1 = (float)x + 1.0f;
		float y0 = (float)y + 0.0f;
		float y1 = (float)y + 1.0f;
		float z0 = (float)z + 0.0f;
		float z1 = (float)z + 1.0f;
		if (face == 0) {
			t.vertexUV(x0, y0, z1, u0, v1);
			t.vertexUV(x0, y0, z0, u0, v0);
			t.vertexUV(x1, y0, z0, u1, v0);
			t.vertexUV(x1, y0, z1, u1, v1);
		}
		if (face == 1) {
			t.vertexUV(x1, y1, z1, u1, v1);
			t.vertexUV(x1, y1, z0, u1, v0);
			t.vertexUV(x0, y1, z0, u0, v0);
			t.vertexUV(x0, y1, z1, u0, v1);
		}
		if (face == 2) {
			t.vertexUV(x0, y1, z0, u1, v0);
			t.vertexUV(x1, y1, z0, u0, v0);
			t.vertexUV(x1, y0, z0, u0, v1);
			t.vertexUV(x0, y0, z0, u1, v1);
		}
		if (face == 3) {
			t.vertexUV(x0, y1, z1, u0, v0);
			t.vertexUV(x0, y0, z1, u0, v1);
			t.vertexUV(x1, y0, z1, u1, v1);
			t.vertexUV(x1, y1, z1, u1, v0);
		}
		if (face == 4) {
			t.vertexUV(x0, y1, z1, u1, v0);
			t.vertexUV(x0, y1, z0, u0, v0);
			t.vertexUV(x0, y0, z0, u0, v1);
			t.vertexUV(x0, y0, z1, u1, v1);
		}
		if (face == 5) {
			t.vertexUV(x1, y0, z1, u0, v1);
			t.vertexUV(x1, y0, z0, u1, v1);
			t.vertexUV(x1, y1, z0, u1, v0);
			t.vertexUV(x1, y1, z1, u0, v0);
		}
	}

	public void renderFaceNoTexture(Tesselator t, int x, int y, int z, int face) {
		float x0 = (float)x + 0.0f;
		float x1 = (float)x + 1.0f;
		float y0 = (float)y + 0.0f;
		float y1 = (float)y + 1.0f;
		float z0 = (float)z + 0.0f;
		float z1 = (float)z + 1.0f;
		if (face == 0) {
			t.vertex(x0, y0, z1);
			t.vertex(x0, y0, z0);
			t.vertex(x1, y0, z0);
			t.vertex(x1, y0, z1);
		}
		if (face == 1) {
			t.vertex(x1, y1, z1);
			t.vertex(x1, y1, z0);
			t.vertex(x0, y1, z0);
			t.vertex(x0, y1, z1);
		}
		if (face == 2) {
			t.vertex(x0, y1, z0);
			t.vertex(x1, y1, z0);
			t.vertex(x1, y0, z0);
			t.vertex(x0, y0, z0);
		}
		if (face == 3) {
			t.vertex(x0, y1, z1);
			t.vertex(x0, y0, z1);
			t.vertex(x1, y0, z1);
			t.vertex(x1, y1, z1);
		}
		if (face == 4) {
			t.vertex(x0, y1, z1);
			t.vertex(x0, y1, z0);
			t.vertex(x0, y0, z0);
			t.vertex(x0, y0, z1);
		}
		if (face == 5) {
			t.vertex(x1, y0, z1);
			t.vertex(x1, y0, z0);
			t.vertex(x1, y1, z0);
			t.vertex(x1, y1, z1);
		}
	}

	public AABB getAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}

	public boolean blocksLight() {
		return true;
	}

	public boolean isSolid() {
		return true;
	}

	public void tick(Level level, int x, int y, int z, Random random) {
	}

	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
		boolean changed = level.setTile(x, y, z, 0);
		if (!changed) return;

		int SD = 4;
		int xx = 0;
		while (xx < SD) {
			int yy = 0;
			while (yy < SD) {
				int zz = 0;
				while (zz < SD) {
					float xp = (float)x + ((float)xx + 0.5f) / (float)SD;
					float yp = (float)y + ((float)yy + 0.5f) / (float)SD;
					float zp = (float)z + ((float)zz + 0.5f) / (float)SD;
					particleEngine.add(new Particle(level, xp, yp, zp, xp - (float)x - 0.5f, yp - (float)y - 0.5f, zp - (float)z - 0.5f, this.tex));
					++zz;
				}
				++yy;
			}
			++xx;
		}
	}
}

