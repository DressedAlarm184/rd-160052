/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.rubydung.particle;

import com.mojang.rubydung.Player;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tessellator;
import com.mojang.rubydung.particle.Particle;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class ParticleEngine {
	protected Level level;
	private List<Particle> particles = new ArrayList<Particle>();

	public ParticleEngine(Level level) {
		this.level = level;
	}

	public void add(Particle p) {
		this.particles.add(p);
	}

	public void tick() {
		int i = 0;
		while (i < this.particles.size()) {
			Particle p = this.particles.get(i);
			p.tick();
			if (p.removed) {
				this.particles.remove(i--);
			}
			++i;
		}
	}

	public void render(Player player, float a, int layer) {
		GL11.glEnable((int)3553);
		int id = Textures.loadTexture("/assets/terrain.png", 9728);
		GL11.glBindTexture((int)3553, (int)id);
		float xa = -((float)Math.cos((double)player.yRot * Math.PI / 180.0));
		float za = -((float)Math.sin((double)player.yRot * Math.PI / 180.0));
		float ya = 1.0f;
		Tessellator t = Tessellator.instance;
		GL11.glColor4f((float)0.8f, (float)0.8f, (float)0.8f, (float)1.0f);
		t.init();
		int i = 0;
		while (i < this.particles.size()) {
			Particle p = this.particles.get(i);
			if (p.isLit() ^ layer == 1) {
				p.render(t, a, xa, ya, za);
			}
			++i;
		}
		t.flush();
		GL11.glDisable((int)3553);
	}
}

