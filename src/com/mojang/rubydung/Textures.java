/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package com.mojang.rubydung;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Textures {
	private static HashMap<String, Integer> idMap = new HashMap();

	public static int loadTexture(String resourceName, int mode) {
		try {
			if (idMap.containsKey(resourceName)) {
				return idMap.get(resourceName);
			}
			IntBuffer ib = BufferUtils.createIntBuffer((int)1);
			ib.clear();
			GL11.glGenTextures((IntBuffer)ib);
			int id = ib.get(0);
			idMap.put(resourceName, id);
			System.out.println(String.valueOf(resourceName) + " -> " + id);
			GL11.glBindTexture((int)3553, (int)id);
			GL11.glTexParameteri((int)3553, (int)10241, (int)mode);
			GL11.glTexParameteri((int)3553, (int)10240, (int)mode);
			BufferedImage img = ImageIO.read(Textures.class.getResourceAsStream(resourceName));
			int w = img.getWidth();
			int h = img.getHeight();
			ByteBuffer pixels = BufferUtils.createByteBuffer((int)(w * h * 4));
			int[] rawPixels = new int[w * h];
			img.getRGB(0, 0, w, h, rawPixels, 0, w);
			int i = 0;
			while (i < rawPixels.length) {
				int a = rawPixels[i] >> 24 & 0xFF;
				int r = rawPixels[i] >> 16 & 0xFF;
				int g = rawPixels[i] >> 8 & 0xFF;
				int b = rawPixels[i] & 0xFF;
				rawPixels[i] = a << 24 | b << 16 | g << 8 | r;
				++i;
			}
			pixels.asIntBuffer().put(rawPixels);
			GLU.gluBuild2DMipmaps((int)3553, (int)6408, (int)w, (int)h, (int)6408, (int)5121, (ByteBuffer)pixels);
			return id;
		}
		catch (IOException e) {
			throw new RuntimeException("!!");
		}
	}
}

