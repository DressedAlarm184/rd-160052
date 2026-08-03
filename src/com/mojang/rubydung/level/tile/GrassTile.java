/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.tile.Tile;
import java.util.Random;

public class GrassTile
extends Tile {
    protected GrassTile(int id) {
        super(id);
        this.tex = 3;
    }

    @Override
    protected int getTexture(int face) {
        if (face == 1) {
            return 0;
        }
        if (face == 0) {
            return 2;
        }
        return 3;
    }

    @Override
    public void tick(Level level, int x, int y, int z, Random random) {
        if (!level.isLit(x, y, z)) {
            level.setTile(x, y, z, Tile.dirt.id);
        } else {
            int i = 0;
            while (i < 4) {
                int zt;
                int yt;
                int xt = x + random.nextInt(3) - 1;
                if (level.getTile(xt, yt = y + random.nextInt(5) - 3, zt = z + random.nextInt(3) - 1) == Tile.dirt.id && level.isLit(xt, yt, zt)) {
                    level.setTile(xt, yt, zt, Tile.grass.id);
                }
                ++i;
            }
        }
    }
}

