package com.mojang.rubydung;
import org.lwjgl.opengl.GL11;

public class FontRenderer {
	private static final int CHAR_SIZE = 8;
	private static final int GRID_SIZE = 16;
	private static final float UV_STEP = 1.0f / GRID_SIZE;
	private static final int SCALE = 2;

	public static void draw(float x, float y, String text) {
		int id = Textures.loadTexture("/assets/default.gif", 9728);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, 0);
		GL11.glScalef(SCALE, SCALE, 1.0f);

		GL11.glBegin(GL11.GL_QUADS);
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			int col = c % GRID_SIZE;
			int row = c / GRID_SIZE;
			
			float u = col * UV_STEP;
			float v = row * UV_STEP;
			
			float drawX = i * CHAR_SIZE;

			GL11.glTexCoord2f(u, v);
			GL11.glVertex2f(drawX, 0);
			
			GL11.glTexCoord2f(u, v + UV_STEP);
			GL11.glVertex2f(drawX, CHAR_SIZE);
			
			GL11.glTexCoord2f(u + UV_STEP, v + UV_STEP);
			GL11.glVertex2f(drawX + CHAR_SIZE, CHAR_SIZE);
			
			GL11.glTexCoord2f(u + UV_STEP, v);
			GL11.glVertex2f(drawX + CHAR_SIZE, 0);
		}
		
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
}
