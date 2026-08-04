package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;

public class TransparentTile extends Tile {
	protected TransparentTile(int id, int tex) {
		super(id, tex);
	}

	@Override public boolean blocksLight() {
		return false;
	}
}
