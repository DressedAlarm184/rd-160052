package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.particle.ParticleEngine;
import com.mojang.rubydung.level.tile.Tile;

public class UnbreakableTile extends Tile {
	protected UnbreakableTile(int id, int tex) {
		super(id, tex);
	}

	@Override
	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {

	}
}
