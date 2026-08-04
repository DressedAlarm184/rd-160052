/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package com.mojang.rubydung;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.level.Level;
import org.lwjgl.input.Keyboard;

public class Player
extends Entity {
	public boolean isFlying = false;

	public Player(Level level) {
		super(level);
		this.heightOffset = 1.62f;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float xa = 0.0f;
		float ya = 0.0f;
		float fly_yd = 0.0f;
		if (Keyboard.isKeyDown((int)200) || Keyboard.isKeyDown((int)17)) {
			ya -= 1.0f;
		}
		if (Keyboard.isKeyDown((int)208) || Keyboard.isKeyDown((int)31)) {
			ya += 1.0f;
		}
		if (Keyboard.isKeyDown((int)203) || Keyboard.isKeyDown((int)30)) {
			xa -= 1.0f;
		}
		if (Keyboard.isKeyDown((int)205) || Keyboard.isKeyDown((int)32)) {
			xa += 1.0f;
		}
		if ((Keyboard.isKeyDown((int)57) || Keyboard.isKeyDown((int)219))) { // Space
			if (this.onGround) this.yd = 0.5f;
			if (this.isFlying) fly_yd = 0.5f;
		}
		if (Keyboard.isKeyDown((int)42) && this.isFlying) fly_yd = -0.5f; // Shift
		this.moveRelative(xa, ya, this.isFlying ? 0.2f : this.onGround ? 0.1f : 0.02f);
		if (!this.isFlying) {
			this.yd = (float)((double)this.yd - 0.08);
			this.move(this.xd, this.yd, this.zd);
		} else {
			this.move(this.xd, fly_yd, this.zd);
		}
		this.xd *= 0.91f;
		this.yd *= 0.98f;
		this.zd *= 0.91f;
		if (this.onGround || this.isFlying) {
			this.xd *= 0.7f;
			this.zd *= 0.7f;
		}
		if (this.y < -50) {
			this.resetPos();
		}
	}

	@Override public void resetPos() {
		super.resetPos();
		this.setRot(0, 0);
	}

	@Override public void move(float xa, float ya, float za) {
		super.move(xa, ya, za);
		if (this.isFlying) this.onGround = false;
	}
}

