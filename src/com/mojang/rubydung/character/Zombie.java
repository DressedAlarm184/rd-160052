/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.rubydung.character;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.character.ZombieModel;
import com.mojang.rubydung.level.Level;
import org.lwjgl.opengl.GL11;

public class Zombie
extends Entity {
    public float rot;
    public float timeOffs;
    public float speed;
    public float rotA = (float)(Math.random() + 1.0) * 0.01f;
    private static ZombieModel zombieModel = new ZombieModel();

    public Zombie(Level level, float x, float y, float z) {
        super(level);
        this.setPos(x, y, z);
        this.timeOffs = (float)Math.random() * 1239813.0f;
        this.rot = (float)(Math.random() * Math.PI * 2.0);
        this.speed = 1.0f;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float xa = 0.0f;
        float ya = 0.0f;
        if (this.y < -100.0f) {
            this.remove();
        }
        this.rot += this.rotA;
        this.rotA = (float)((double)this.rotA * 0.99);
        this.rotA = (float)((double)this.rotA + (Math.random() - Math.random()) * Math.random() * Math.random() * (double)0.08f);
        xa = (float)Math.sin(this.rot);
        ya = (float)Math.cos(this.rot);
        if (this.onGround && Math.random() < 0.08) {
            this.yd = 0.5f;
        }
        this.moveRelative(xa, ya, this.onGround ? 0.1f : 0.02f);
        this.yd = (float)((double)this.yd - 0.08);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.91f;
        this.yd *= 0.98f;
        this.zd *= 0.91f;
        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }

    public void render(float a) {
        GL11.glEnable((int)3553);
        GL11.glBindTexture((int)3553, (int)Textures.loadTexture("/char.png", 9728));
        GL11.glPushMatrix();
        double time = (double)System.nanoTime() / 1.0E9 * 10.0 * (double)this.speed + (double)this.timeOffs;
        float size = 0.058333334f;
        float yy = (float)(-Math.abs(Math.sin(time * 0.6662)) * 5.0 - 23.0);
        GL11.glTranslatef((float)(this.xo + (this.x - this.xo) * a), (float)(this.yo + (this.y - this.yo) * a), (float)(this.zo + (this.z - this.zo) * a));
        GL11.glScalef((float)1.0f, (float)-1.0f, (float)1.0f);
        GL11.glScalef((float)size, (float)size, (float)size);
        GL11.glTranslatef((float)0.0f, (float)yy, (float)0.0f);
        float c = 57.29578f;
        GL11.glRotatef((float)(this.rot * c + 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        zombieModel.render((float)time);
        GL11.glPopMatrix();
        GL11.glDisable((int)3553);
    }
}

