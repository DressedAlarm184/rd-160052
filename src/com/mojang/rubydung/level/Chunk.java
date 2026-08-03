/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.rubydung.level;

import com.mojang.rubydung.Player;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tesselator;
import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.phys.AABB;
import org.lwjgl.opengl.GL11;

public class Chunk {
    public AABB aabb;
    public final Level level;
    public final int x0;
    public final int y0;
    public final int z0;
    public final int x1;
    public final int y1;
    public final int z1;
    public final float x;
    public final float y;
    public final float z;
    private boolean dirty = true;
    private int lists = -1;
    public long dirtiedTime = 0L;
    private static Tesselator t = Tesselator.instance;
    public static int updates = 0;
    private static long totalTime = 0L;
    private static int totalUpdates = 0;

    public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
        this.level = level;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x = (float)(x0 + x1) / 2.0f;
        this.y = (float)(y0 + y1) / 2.0f;
        this.z = (float)(z0 + z1) / 2.0f;
        this.aabb = new AABB(x0, y0, z0, x1, y1, z1);
        this.lists = GL11.glGenLists((int)2);
    }

    private void rebuild(int layer) {
        this.dirty = false;
        ++updates;
        long before = System.nanoTime();
        GL11.glNewList((int)(this.lists + layer), (int)4864);
        t.init();
        int tiles = 0;
        int x = this.x0;
        while (x < this.x1) {
            int y = this.y0;
            while (y < this.y1) {
                int z = this.z0;
                while (z < this.z1) {
                    int tileId = this.level.getTile(x, y, z);
                    if (tileId > 0) {
                        Tile.tiles[tileId].render(t, this.level, layer, x, y, z);
                        ++tiles;
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        t.flush();
        GL11.glEndList();
        long after = System.nanoTime();
        if (tiles > 0) {
            totalTime += after - before;
            ++totalUpdates;
        }
    }

    public void rebuild() {
        this.rebuild(0);
        this.rebuild(1);
    }

    public void render(int layer) {
        GL11.glCallList((int)(this.lists + layer));
    }

    public void setDirty() {
        if (!this.dirty) {
            this.dirtiedTime = System.currentTimeMillis();
        }
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public float distanceToSqr(Player player) {
        float xd = player.x - this.x;
        float yd = player.y - this.y;
        float zd = player.z - this.z;
        return xd * xd + yd * yd + zd * zd;
    }
}

