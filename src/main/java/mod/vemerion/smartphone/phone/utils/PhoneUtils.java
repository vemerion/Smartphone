package mod.vemerion.smartphone.phone.utils;

import java.awt.Color;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mod.vemerion.smartphone.Main;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class PhoneUtils {

	public static final ResourceLocation WHITE_PIXEL = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");

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

	public static final int WALLPAPER_WIDTH = 16; // The typical width in pixels of the phone wallpaper
	public static final int WALLPAPER_HEIGHT = 32; // The typical height in pixels of the phone wallpaper

	public static void draw(ResourceLocation texture, float x, float y, float width, float height, float texX,
			float texY, float texWidth, float texHeight, Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int a = color.getAlpha();
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(texture);
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
		builder.pos(x, y + height, Z).color(r, g, b, a).tex(texX, texY + texHeight).endVertex();
		builder.pos(x + width, y + height, Z).color(r, g, b, a).tex(texX + texWidth, texY + texHeight).endVertex();
		builder.pos(x + width, y, Z).color(r, g, b, a).tex(texX + texWidth, texY).endVertex();
		builder.pos(x, y, Z).color(r, g, b, a).tex(texX, texY).endVertex();
		builder.finishDrawing();
		RenderSystem.enableBlend();

		WorldVertexBufferUploader.draw(builder);
	}

	public static void draw(ResourceLocation texture, float x, float y, float width, float height) {
		draw(texture, x, y, width, height, 0, 0, 1, 1, Color.WHITE);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height, float texX,
			float texY, float texWidth, float texHeight, Color color) {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		float windowWidth = window.getScaledWidth();
		float windowHeight = window.getScaledHeight();
		x = windowWidth * 0.5f - SCREEN_HORIZONTAL_CENTER_OFFSET + (x / APP_WIDTH) * SCREEN_WIDTH;
		y = windowHeight - SCREEN_BOTTON_OFFSET - SCREEN_HEIGHT + (y / APP_HEIGHT) * SCREEN_HEIGHT;
		width = (width / APP_WIDTH) * SCREEN_WIDTH;
		height = (height / APP_HEIGHT) * SCREEN_HEIGHT;

		draw(texture, x, y, width, height, texX, texY, texWidth, texHeight, color);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height, float texX,
			float texY, float texWidth, float texHeight) {
		drawOnPhone(texture, x, y, width, height, texX, texY, texWidth, texHeight, Color.WHITE);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height) {
		drawOnPhone(texture, x, y, width, height, 0, 0, 1, 1);
	}

	public static void drawOnPhone(ResourceLocation texture, float x, float y, float width, float height, Color color) {
		drawOnPhone(texture, x, y, width, height, 0, 0, 1, 1, color);
	}

	public static void drawWallpaper(int[][] wallpaper, float left, float top, float width, float height) {
		if (wallpaper.length < WALLPAPER_WIDTH || wallpaper[0].length < WALLPAPER_HEIGHT)
			throw new IllegalArgumentException("wallpaper array has invalid size");

		float pixelWidth = width / WALLPAPER_WIDTH;
		float pixelHeight = height / WALLPAPER_HEIGHT;

		for (int x = 0; x < wallpaper.length; x++) {
			for (int y = 0; y < wallpaper[x].length; y++) {
				drawOnPhone(WHITE_PIXEL, left + x * pixelWidth, top + y * pixelHeight, pixelWidth, pixelHeight,
						new Color(wallpaper[x][y]));
			}
		}
	}

	public static void writeOnPhone(MatrixStack matrix, FontRenderer font, IReorderingProcessor text, float x, float y,
			Color color, float size, boolean center) {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		float windowWidth = window.getScaledWidth();
		float windowHeight = window.getScaledHeight();
		x = windowWidth * 0.5f - SCREEN_HORIZONTAL_CENTER_OFFSET + (x / APP_WIDTH) * SCREEN_WIDTH;
		y = windowHeight - SCREEN_BOTTON_OFFSET - SCREEN_HEIGHT + (y / APP_HEIGHT) * SCREEN_HEIGHT;

		matrix.push();
		matrix.translate(x - (center ? font.func_243245_a(text) * size / 2 : 0), y, 0);
		matrix.scale(size, size, size);

		font.func_238422_b_(matrix, text, 0, 0, color.getRGB());

		matrix.pop();
	}

	public static void writeOnPhone(MatrixStack matrix, FontRenderer font, String text, float x, float y, Color color,
			float size, boolean center) {
		List<IReorderingProcessor> lines = font.trimStringToWidth(new StringTextComponent(text), Integer.MAX_VALUE);
		if (!lines.isEmpty())
			writeOnPhone(matrix, font, lines.get(0), x, y, color, size, center);
	}

	public static void writeOnPhoneTrim(MatrixStack matrix, FontRenderer font, String text, float x, float y,
			Color color, float size, float width, boolean reverse, boolean center) {
		int realWidth = (int) fromVirtualWidth(width / size);
		List<IReorderingProcessor> lines = font
				.trimStringToWidth(new StringTextComponent(font.func_238413_a_(text, realWidth, reverse)), realWidth);
		if (!lines.isEmpty())
			writeOnPhone(matrix, font, lines.get(0), x, y, color, size, center);
	}

	public static void writeOnPhoneWrap(MatrixStack matrix, FontRenderer font, String text, float x, float y,
			Color color, float size, float width, boolean center) {
		List<IReorderingProcessor> lines = font.trimStringToWidth(new StringTextComponent(text),
				(int) fromVirtualWidth(width / size));
		for (int i = 0; i < lines.size(); i++) {
			writeOnPhone(matrix, font, lines.get(i), x, y + font.FONT_HEIGHT * i * size * 1.3f, color, size, center);
		}
	}
	
	public static int textHeight(FontRenderer font, String text, float size, float width) {
		return (int) (font.getWordWrappedHeight(text, (int) (PhoneUtils.fromVirtualWidth(width) / size)) * size * 1.3f);
	}
	
	// converts virtual app width to window width
	public static float fromVirtualWidth(float width) {
		return width / APP_WIDTH * SCREEN_WIDTH;
	}

	// converts window width to virtual app width
	public static float toVirtualWidth(float width) {
		return width * APP_WIDTH / SCREEN_WIDTH;
	}
	
	public static float toVirtualHeight(float height) {
		return height * APP_HEIGHT / SCREEN_HEIGHT;
	}
}
