package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.tile.Tile;

public class MultiTile extends Tile {
	private int[] texs = new int[6];

	protected MultiTile(int id, int t0, int t1, int t2, int t3, int t4, int t5) {
		super(id);
		this.tex = t0;
		this.texs[0] = t0;
		this.texs[1] = t1;
		this.texs[2] = t2;
		this.texs[3] = t3;
		this.texs[4] = t4;
		this.texs[5] = t5;
	}

	@Override protected int getTexture(int face) {
		return this.texs[face];
	}
}
