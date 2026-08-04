/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package com.mojang.rubydung;

import com.mojang.rubydung.HitResult;
import com.mojang.rubydung.Player;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.Timer;
import com.mojang.rubydung.character.Zombie;
import com.mojang.rubydung.level.Chunk;
import com.mojang.rubydung.level.Frustum;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelRenderer;
import com.mojang.rubydung.level.Tesselator;
import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.particle.ParticleEngine;
import com.mojang.rubydung.phys.AABB;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.DisplayMode;
import java.nio.file.Files;
import java.nio.file.Path;

public class RubyDung
implements Runnable {
	private int width;
	private int height;
	private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer((int)4);
	private FloatBuffer fogColor1 = BufferUtils.createFloatBuffer((int)4);
	private Timer timer = new Timer(20.0f);
	private Level level;
	private LevelRenderer levelRenderer;
	private Player player;
	private int paintTexture = 1;
	private ParticleEngine particleEngine;
	private ArrayList<Zombie> zombies = new ArrayList();
	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer((int)16);
	private IntBuffer selectBuffer = BufferUtils.createIntBuffer((int)2000);
	private HitResult hitResult = null;
	FloatBuffer lb = BufferUtils.createFloatBuffer((int)16);
	private int active_texture = 0;
	private int[] textures = {1, 3, 4, 5, 6};

	static Level create_level() {
		if (!Files.exists(Path.of("size.txt")) || !Files.exists(Path.of("level.dat"))) {
			String size = JOptionPane.showInputDialog("Enter level size (Format: X,Y,Z)", "256,64,256");
			if (size == null || size.trim().isEmpty()) System.exit(0);
			try {
				Files.writeString(Path.of("size.txt"), size);
			} catch (Throwable e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		int lvlX, lvlY, lvlZ;
		try {
			String size = Files.readString(Path.of("size.txt"));
			String[] sizes = size.split(",");
			for (int i = 0; i < sizes.length; i++) {
				sizes[i] = sizes[i].trim();
			}
			lvlX = Integer.valueOf(sizes[0]);
			lvlY = Integer.valueOf(sizes[1]);
			lvlZ = Integer.valueOf(sizes[2]);
			return new Level(lvlX, lvlZ, lvlY);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
		throw new AssertionError("Unreachable");
	}

	public void init() throws LWJGLException, IOException {
		this.level = create_level();
		int col0 = 16710650;
		int col1 = 920330;
		float fr = 0.5f;
		float fg = 0.8f;
		float fb = 1.0f;
		this.fogColor0.put(new float[]{(float)(col0 >> 16 & 0xFF) / 255.0f, (float)(col0 >> 8 & 0xFF) / 255.0f, (float)(col0 & 0xFF) / 255.0f, 1.0f});
		this.fogColor0.flip();
		this.fogColor1.put(new float[]{(float)(col1 >> 16 & 0xFF) / 255.0f, (float)(col1 >> 8 & 0xFF) / 255.0f, (float)(col1 & 0xFF) / 255.0f, 1.0f});
		this.fogColor1.flip();
		this.width = 800;
		this.height = 600;
		Display.setDisplayMode(new DisplayMode(this.width, this.height));
		Display.create();
		Keyboard.create();
		Mouse.create();
		GL11.glEnable((int)3553);
		GL11.glShadeModel((int)7425);
		GL11.glClearColor((float)fr, (float)fg, (float)fb, (float)0.0f);
		GL11.glClearDepth((double)1.0);
		GL11.glEnable((int)2929);
		GL11.glDepthFunc((int)515);
		GL11.glMatrixMode((int)5889);
		GL11.glLoadIdentity();
		GL11.glMatrixMode((int)5888);
		this.levelRenderer = new LevelRenderer(this.level);
		this.player = new Player(this.level);
		this.particleEngine = new ParticleEngine(this.level);
		Mouse.setGrabbed((boolean)true);
		int i = 0;
		while (i < 10) {
			Zombie zombie = new Zombie(this.level, 128.0f, 0.0f, 128.0f);
			zombie.resetPos();
			this.zombies.add(zombie);
			++i;
		}
	}

	public void destroy() {
		this.level.save();
		Mouse.destroy();
		Keyboard.destroy();
		Display.destroy();
	}

	/*
	 * Handled impossible loop by duplicating code
	 * Enabled aggressive block sorting
	 * Enabled unnecessary exception pruning
	 * Enabled aggressive exception aggregation
	 */
	@Override
	public void run() {
		try {
			this.init();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Failed to start RubyDung", 0);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int frames = 0;
		try {
			try {
				block12: {
					block11: {
						if (!true) break block11;
						if (Keyboard.isKeyDown((int)1)) return;
						if (Display.isCloseRequested()) break block12;
					}
					do {
						this.timer.advanceTime();
						int i = 0;
						while (i < this.timer.ticks) {
							this.tick();
							++i;
						}
						this.render(this.timer.a);
						++frames;
						while (System.currentTimeMillis() >= lastTime + 1000L) {
							Chunk.updates = 0;
							lastTime += 1000L;
							frames = 0;
						}
						if (Keyboard.isKeyDown((int)1)) return;
					} while (!Display.isCloseRequested());
				}
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
				this.destroy();
				return;
			}
		}
		finally {
			this.destroy();
		}
	}

	public void tick() {
		int wheel = Mouse.getDWheel();

		if (wheel < 0) {
			this.active_texture++;
			if (this.active_texture >= textures.length) this.active_texture = 0;
		} else if (wheel > 0) {
			this.active_texture--;
			if (this.active_texture < 0) this.active_texture = textures.length - 1;
		}

		this.paintTexture = textures[this.active_texture];

		while (Keyboard.next()) {
			if (!Keyboard.getEventKeyState()) continue;
			int key = Keyboard.getEventKey();
			if (key == 28) { // Enter
				this.level.save();
			} else if (key == 34) { // G
				this.zombies.add(new Zombie(this.level, this.player.x, this.player.y, this.player.z));
			} else if (key == 19) { // R
				player.resetPos();
			} else if (key == 33) { // F
				player.isFlying = !player.isFlying;
			}
		}

		this.level.tick();
		this.particleEngine.tick();
		int i = 0;
		while (i < this.zombies.size()) {
			this.zombies.get(i).tick();
			if (this.zombies.get((int)i).removed) {
				this.zombies.remove(i--);
			}
			++i;
		}
		this.player.tick();
	}

	private void moveCameraToPlayer(float a) {
		GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.3f);
		GL11.glRotatef((float)this.player.xRot, (float)1.0f, (float)0.0f, (float)0.0f);
		GL11.glRotatef((float)this.player.yRot, (float)0.0f, (float)1.0f, (float)0.0f);
		float x = this.player.xo + (this.player.x - this.player.xo) * a;
		float y = this.player.yo + (this.player.y - this.player.yo) * a;
		float z = this.player.zo + (this.player.z - this.player.zo) * a;
		GL11.glTranslatef((float)(-x), (float)(-y), (float)(-z));
	}

	private void setupCamera(float a) {
		GL11.glMatrixMode((int)5889);
		GL11.glLoadIdentity();
		GLU.gluPerspective((float)70.0f, (float)((float)this.width / (float)this.height), (float)0.05f, (float)1000.0f);
		GL11.glMatrixMode((int)5888);
		GL11.glLoadIdentity();
		this.moveCameraToPlayer(a);
	}

	private void setupOrthoCamera() {
		GL11.glMatrixMode((int)5889);
		GL11.glLoadIdentity();
		GL11.glOrtho((double)0.0, (double)this.width, (double)this.height, (double)0.0, (double)100.0, (double)300.0);
		GL11.glMatrixMode((int)5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-200.0f);
	}

	private void setupPickCamera(float a, int x, int y) {
		GL11.glMatrixMode((int)5889);
		GL11.glLoadIdentity();
		this.viewportBuffer.clear();
		GL11.glGetInteger((int)2978, (IntBuffer)this.viewportBuffer);
		this.viewportBuffer.flip();
		this.viewportBuffer.limit(16);
		GLU.gluPickMatrix((float)x, (float)y, (float)5.0f, (float)5.0f, (IntBuffer)this.viewportBuffer);
		GLU.gluPerspective((float)70.0f, (float)((float)this.width / (float)this.height), (float)0.05f, (float)1000.0f);
		GL11.glMatrixMode((int)5888);
		GL11.glLoadIdentity();
		this.moveCameraToPlayer(a);
	}

	private void pick(float a) {
		this.selectBuffer.clear();
		GL11.glSelectBuffer((IntBuffer)this.selectBuffer);
		GL11.glRenderMode((int)7170);
		this.setupPickCamera(a, this.width / 2, this.height / 2);
		this.levelRenderer.pick(this.player);
		int hits = GL11.glRenderMode((int)7168);
		this.selectBuffer.flip();
		this.selectBuffer.limit(this.selectBuffer.capacity());
		long closest = 0L;
		int[] names = new int[10];
		int hitNameCount = 0;
		int i = 0;
		while (i < hits) {
			int j;
			int nameCount = this.selectBuffer.get();
			long minZ = this.selectBuffer.get();
			this.selectBuffer.get();
			long dist = minZ;
			if (dist < closest || i == 0) {
				closest = dist;
				hitNameCount = nameCount;
				j = 0;
				while (j < nameCount) {
					names[j] = this.selectBuffer.get();
					++j;
				}
			} else {
				j = 0;
				while (j < nameCount) {
					this.selectBuffer.get();
					++j;
				}
			}
			++i;
		}
		this.hitResult = hitNameCount > 0 ? new HitResult(names[0], names[1], names[2], names[3], names[4]) : null;
	}

	public void render(float a) {
		float xo = Mouse.getDX();
		float yo = Mouse.getDY();
		this.player.turn(xo, yo);
		this.pick(a);
		while (Mouse.next()) {
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
				Tile oldTile = Tile.tiles[this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z)];
				if (oldTile != null) oldTile.destroy(this.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
			}
			if (Mouse.getEventButton() != 1 || !Mouse.getEventButtonState() || this.hitResult == null) continue;
			int x = this.hitResult.x;
			int y = this.hitResult.y;
			int z = this.hitResult.z;
			if (this.hitResult.f == 0) {
				--y;
			}
			if (this.hitResult.f == 1) {
				++y;
			}
			if (this.hitResult.f == 2) {
				--z;
			}
			if (this.hitResult.f == 3) {
				++z;
			}
			if (this.hitResult.f == 4) {
				--x;
			}
			if (this.hitResult.f == 5) {
				++x;
			}
			AABB aabb = new AABB(x, y, z, x + 1, y + 1, z + 1);
			if (!aabb.intersects(this.player.bb)) {
				this.level.setTile(x, y, z, this.paintTexture);
			}
		}
		GL11.glClear((int)16640);
		this.setupCamera(a);
		GL11.glEnable((int)2884);
		Frustum frustum = Frustum.getFrustum();
		this.levelRenderer.updateDirtyChunks(this.player);
		this.setupFog(0);
		GL11.glEnable((int)2912);
		this.levelRenderer.render(this.player, 0);
		int i = 0;
		while (i < this.zombies.size()) {
			Zombie zombie = this.zombies.get(i);
			if (zombie.isLit() && frustum.isVisible(zombie.bb)) {
				this.zombies.get(i).render(a);
			}
			++i;
		}
		this.particleEngine.render(this.player, a, 0);
		this.setupFog(1);
		this.levelRenderer.render(this.player, 1);
		i = 0;
		while (i < this.zombies.size()) {
			Zombie zombie = this.zombies.get(i);
			if (!zombie.isLit() && frustum.isVisible(zombie.bb)) {
				this.zombies.get(i).render(a);
			}
			++i;
		}
		this.particleEngine.render(this.player, a, 1);
		GL11.glDisable((int)2896);
		GL11.glDisable((int)3553);
		GL11.glDisable((int)2912);
		if (this.hitResult != null) {
			this.levelRenderer.renderHit(this.hitResult);
		}
		this.drawGui(a);
		Display.update();
	}

	private void drawGui(float a) {
		GL11.glClear((int)256);
		this.setupOrthoCamera();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(this.width - 48), (float)48.0f, (float)0.0f);
		Tesselator t = Tesselator.instance;
		GL11.glScalef((float)48.0f, (float)-48.0f, (float)48.0f);
		GL11.glRotatef((float)30.0f, (float)1.0f, (float)0.0f, (float)0.0f);
		GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
		GL11.glTranslatef((float)1.5f, (float)-0.5f, (float)-0.5f);
		int id = Textures.loadTexture("/assets/terrain.png", 9728);
		GL11.glBindTexture((int)3553, (int)id);
		GL11.glEnable((int)3553);
		t.init();
		Tile.tiles[this.paintTexture].render(t, this.level, 0, -2, 0, 0);
		t.flush();
		GL11.glDisable((int)3553);
		GL11.glPopMatrix();
		int wc = this.width / 2;
		int hc = this.height / 2;
		GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
		t.init();
		t.vertex(wc + 1, hc - 12, 0.0f);
		t.vertex(wc - 1, hc - 12, 0.0f);
		t.vertex(wc - 1, hc + 12, 0.0f);
		t.vertex(wc + 1, hc + 12, 0.0f);
		t.vertex(wc + 12, hc - 1, 0.0f);
		t.vertex(wc - 12, hc - 1, 0.0f);
		t.vertex(wc - 12, hc + 1, 0.0f);
		t.vertex(wc + 12, hc + 1, 0.0f);
		t.flush();
	}

	private void setupFog(int i) {
		if (i == 0) {
			GL11.glFogi((int)2917, (int)2048);
			GL11.glFogf((int)2914, (float)0.001f);
			GL11.glFog((int)2918, (FloatBuffer)this.fogColor0);
			GL11.glDisable((int)2896);
		} else if (i == 1) {
			GL11.glFogi((int)2917, (int)2048);
			GL11.glFogf((int)2914, (float)0.06f);
			GL11.glFog((int)2918, (FloatBuffer)this.fogColor1);
			GL11.glEnable((int)2896);
			GL11.glEnable((int)2903);
			float br = 0.6f;
			GL11.glLightModel((int)2899, (FloatBuffer)this.getBuffer(br, br, br, 1.0f));
		}
	}

	private FloatBuffer getBuffer(float a, float b, float c, float d) {
		this.lb.clear();
		this.lb.put(a).put(b).put(c).put(d);
		this.lb.flip();
		return this.lb;
	}

	public static void checkError() {
		int e = GL11.glGetError();
		if (e != 0) {
			throw new IllegalStateException(GLU.gluErrorString((int)e));
		}
	}

	public static void main(String[] args) throws LWJGLException {
		new Thread(new RubyDung()).start();
	}
}

