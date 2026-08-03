/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.rubydung.level;

public interface LevelListener {
    public void tileChanged(int var1, int var2, int var3);

    public void lightColumnChanged(int var1, int var2, int var3, int var4);

    public void allChanged();
}

