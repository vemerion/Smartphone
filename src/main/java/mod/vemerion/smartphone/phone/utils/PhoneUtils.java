package mod.vemerion.smartphone.phone.utils;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class PhoneUtils {

	private static final float SCALE = 3; // How much smaller/larger the ingame phone is than the phone texture
	private static final float PHONE_REAL_WIDTH = 32; // The phone texture width
	private static final float PHONE_REAL_HEIGHT = 64; // The phone texture height
	public static final float PHONE_WIDTH = PHONE_REAL_WIDTH * SCALE; // The ingame phone width
	public static final float PHONE_HEIGHT = PHONE_REAL_HEIGHT * SCALE; // The ingame phone height
	public static final float SCREEN_WIDTH = PHONE_WIDTH - 8 * SCALE; // The ingame screen width
	public static final float SCREEN_HEIGHT = PHONE_HEIGHT - 16 * SCALE; // The ingame screen height
	public static final float PHONE_HORIZONTAL_CENTER_OFFSET = PHONE_WIDTH / 2; // The horizontal phone offset from the
																				// center of the screen
	public static final float PHONE_BOTTOM_OFFSET = 32; // The vertical phone offset from the bottom of the screen
	public static final float SCREEN_HORIZONTAL_CENTER_OFFSET = PHONE_HORIZONTAL_CENTER_OFFSET - 4 * SCALE; // The
																											// horizontal
																											// screen
																											// offset
																											// from the
																											// center of
																											// the
																											// screen
	public static final float SCREEN_BOTTON_OFFSET = PHONE_BOTTOM_OFFSET + 8 * SCALE; // The vertical screen offset from
																						// the bottom of the screen
	public static final float APP_WIDTH = 100; // When drawing on screen, this is the virtual width of the screen
	public static final float APP_HEIGHT = 200; // When drawing on screen, this is the virtual height of the screen

	private static final double Z = -5;

	public static void draw(ResourceLocation texture, float x, float y, float width, float height, float texX,
			float texY, float texWidth, float texHeight) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(texture);
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_TEX);
		builder.pos(x, y + height, Z).tex(texX, texY + texHeight).endVertex();
		builder.pos(x + width, y + height, Z).tex(texX + texWidth, texY + texHeight).endVertex();
		builder.pos(x + width, y, Z).tex(texX + texWidth, texY).endVertex();
		builder.pos(x, y, Z).tex(texX, texY).endVertex();
		builder.finishDrawing();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();

		WorldVertexBufferUploader.draw(builder);
	}

	public static void draw(ResourceLocation texture, float x, float y, float width, float height) {
		draw(texture, x, y, width, height, 0, 0, 1, 1);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height, float texX,
			float texY, float texWidth, float texHeight) {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		float windowWidth = window.getScaledWidth();
		float windowHeight = window.getScaledHeight();
		x = windowWidth * 0.5f - SCREEN_HORIZONTAL_CENTER_OFFSET + (x / APP_WIDTH) * SCREEN_WIDTH;
		y = windowHeight - SCREEN_BOTTON_OFFSET - SCREEN_HEIGHT + (y / APP_HEIGHT) * SCREEN_HEIGHT;
		width = (width / APP_WIDTH) * SCREEN_WIDTH;
		height = (height / APP_HEIGHT) * SCREEN_HEIGHT;

		draw(texture, x, y, width, height, texX, texY, texWidth, texHeight);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height) {
		drawOnPhone(texture, x, y, width, height, 0, 0, 1, 1);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height, Color color) {
		RenderSystem.color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
		drawOnPhone(texture, x, y, width, height);
		RenderSystem.color3f(1, 1, 1);
	}

	public static void writeOnPhone(String text, float x, float y, Color color, float size) {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		float windowWidth = window.getScaledWidth();
		float windowHeight = window.getScaledHeight();
		x = windowWidth * 0.5f - SCREEN_HORIZONTAL_CENTER_OFFSET + (x / APP_WIDTH) * SCREEN_WIDTH;
		y = windowHeight - SCREEN_BOTTON_OFFSET - SCREEN_HEIGHT + (y / APP_HEIGHT) * SCREEN_HEIGHT;

		RenderSystem.pushMatrix();
		MatrixStack matrix = new MatrixStack();
		matrix.push();
		matrix.translate(x, y, 0);
		matrix.scale(size, size, size);
		RenderSystem.multMatrix(matrix.getLast().getMatrix());

		Minecraft.getInstance().fontRenderer.drawString(text, 0, 0, color.getRGB());

		RenderSystem.popMatrix();
	}
}