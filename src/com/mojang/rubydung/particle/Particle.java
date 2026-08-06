/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.particle;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tessellator;

public class Particle
extends Entity {
	private float xd;
	private float yd;
	private float zd;
	public int tex;
	private float uo;
	private float vo;

	public Particle(Level level, float x, float y, float z, float xa, float ya, float za, int tex) {
		super(level);
		this.tex = tex;
		this.setSize(0.2f, 0.2f);
		this.heightOffset = this.bbHeight / 2.0f;
		this.setPos(x, y, z);
		this.xd = xa + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
		this.yd = ya + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
		this.zd = za + (float)(Math.random() * 2.0 - 1.0) * 0.4f;
		float speed = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
		float dd = (float)Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
		this.xd = this.xd / dd * speed * 0.7f;
		this.yd = this.yd / dd * speed;
		this.zd = this.zd / dd * speed * 0.7f;
		this.uo = (float)Math.random() * 3.0f;
		this.vo = (float)Math.random() * 3.0f;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (Math.random() < 0.1) {
			this.remove();
		}
		this.yd = (float)((double)this.yd - 0.06);
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.98f;
		this.yd *= 0.98f;
		this.zd *= 0.98f;
		if (this.onGround) {
			this.xd *= 0.7f;
			this.zd *= 0.7f;
		}
	}

	public void render(Tessellator t, float a, float xa, float ya, float za) {
		float u0 = ((float)(this.tex % 16) + this.uo / 4.0f) / 16.0f;
		float u1 = u0 + 0.015609375f;
		float v0 = ((float)(this.tex / 16) + this.vo / 4.0f) / 16.0f;
		float v1 = v0 + 0.015609375f;
		float r = 0.1f;
		float x = this.xo + (this.x - this.xo) * a;
		float y = this.yo + (this.y - this.yo) * a;
		float z = this.zo + (this.z - this.zo) * a;
		t.vertexUV(x - xa * r, y - ya * r, z - za * r, u0, v1);
		t.vertexUV(x - xa * r, y + ya * r, z - za * r, u0, v0);
		t.vertexUV(x + xa * r, y + ya * r, z + za * r, u1, v0);
		t.vertexUV(x + xa * r, y - ya * r, z + za * r, u1, v1);
	}
}

