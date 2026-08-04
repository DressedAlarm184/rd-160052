/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.phys.AABB;
import java.util.ArrayList;

public class Entity {
	protected Level level;
	public float xo;
	public float yo;
	public float zo;
	public float x;
	public float y;
	public float z;
	public float xd;
	public float yd;
	public float zd;
	public float yRot;
	public float xRot;
	public AABB bb;
	public boolean onGround = false;
	public boolean removed = false;
	protected float heightOffset = 0.0f;
	protected float bbWidth = 0.6f;
	protected float bbHeight = 1.8f;

	public Entity(Level level) {
		this.level = level;
		this.resetPos();
	}

	protected void resetPos() {
		float x = (float)Math.random() * (float)this.level.width;
		float y = this.level.depth + 10;
		float z = (float)Math.random() * (float)this.level.height;
		this.setPos(x, y, z);
	}

	public void remove() {
		this.removed = true;
	}

	protected void setSize(float w, float h) {
		this.bbWidth = w;
		this.bbHeight = h;
	}

	protected void setPos(float x, float y, float z) {
		this.x = x;
		this.xd = 0;
		this.y = y;
		this.yd = 0;
		this.z = z;
		this.zd = 0;
		float w = this.bbWidth / 2.0f;
		float h = this.bbHeight / 2.0f;
		this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
	}

	protected void setRot(float xRot, float yRot) {
		this.xRot = xRot;
		this.yRot = yRot;
	}

	public void turn(float xo, float yo) {
		this.yRot = (float)((double)this.yRot + (double)xo * 0.15);
		this.xRot = (float)((double)this.xRot - (double)yo * 0.15);
		if (this.xRot < -90.0f) {
			this.xRot = -90.0f;
		}
		if (this.xRot > 90.0f) {
			this.xRot = 90.0f;
		}
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
	}

	public void move(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;
		ArrayList<AABB> aABBs = this.level.getCubes(this.bb.expand(xa, ya, za));
		int i = 0;
		while (i < aABBs.size()) {
			ya = ((AABB)aABBs.get(i)).clipYCollide(this.bb, ya);
			++i;
		}
		this.bb.move(0.0f, ya, 0.0f);
		i = 0;
		while (i < aABBs.size()) {
			xa = ((AABB)aABBs.get(i)).clipXCollide(this.bb, xa);
			++i;
		}
		this.bb.move(xa, 0.0f, 0.0f);
		i = 0;
		while (i < aABBs.size()) {
			za = ((AABB)aABBs.get(i)).clipZCollide(this.bb, za);
			++i;
		}
		this.bb.move(0.0f, 0.0f, za);
		boolean bl = this.onGround = yaOrg != ya && yaOrg < 0.0f;
		if (xaOrg != xa) {
			this.xd = 0.0f;
		}
		if (yaOrg != ya) {
			this.yd = 0.0f;
		}
		if (zaOrg != za) {
			this.zd = 0.0f;
		}
		this.x = (this.bb.x0 + this.bb.x1) / 2.0f;
		this.y = this.bb.y0 + this.heightOffset;
		this.z = (this.bb.z0 + this.bb.z1) / 2.0f;
	}

	public void moveRelative(float xa, float za, float speed) {
		float dist = xa * xa + za * za;
		if (dist < 0.01f) {
			return;
		}
		dist = speed / (float)Math.sqrt(dist);
		float sin = (float)Math.sin((double)this.yRot * Math.PI / 180.0);
		float cos = (float)Math.cos((double)this.yRot * Math.PI / 180.0);
		this.xd += (xa *= dist) * cos - (za *= dist) * sin;
		this.zd += za * cos + xa * sin;
	}

	public boolean isLit() {
		int xTile = (int)this.x;
		int yTile = (int)this.y;
		int zTile = (int)this.z;
		return this.level.isLit(xTile, yTile, zTile);
	}
}

