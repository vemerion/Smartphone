package mod.vemerion.smartphone.phone.app;

import java.awt.Color;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.MathHelper;

public class WallpaperApp extends App {

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/icon.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation TRANSPARENT = new ResourceLocation(Main.MODID,
			"textures/gui/transparent_background.png");
	private static final ResourceLocation CAMERA = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/camera.png");
	private static final ResourceLocation PAINT = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/paint_brush.png");
	private static final ResourceLocation CAPTURE = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/capture_button.png");
	private static final ResourceLocation CONFIRM = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/confirm_button.png");

	private Button cameraButton;
	private Button paintButton;
	private App subApp;
	private int[][] wallpaper;
	private boolean hasCustomWallpaper;

	public WallpaperApp(Phone phone) {
		super(phone);
		cameraButton = new Button(new Rectangle(0, 0, PhoneUtils.APP_HEIGHT / 2), CAMERA, phone,
				() -> subApp = new CameraApp(phone));
		paintButton = new Button(new Rectangle(0, PhoneUtils.APP_HEIGHT / 2, PhoneUtils.APP_HEIGHT / 2), PAINT, phone,
				() -> subApp = new PaintApp(phone));

		wallpaper = new int[PhoneUtils.WALLPAPER_WIDTH][PhoneUtils.WALLPAPER_HEIGHT];

		for (int x = 0; x < wallpaper.length; x++) {
			for (int y = 0; y < wallpaper[x].length; y++) {
				wallpaper[x][y] = Color.WHITE.getRGB();
			}
		}
	}

	@Override
	public void resume() {
		super.resume();
		subApp = null;
	}

	@Override
	public void tick() {
		super.tick();

		if (subApp != null) {
			subApp.tick();
		} else {
			cameraButton.tick();
			paintButton.tick();
		}
	}

	@Override
	public void render() {
		if (subApp != null) {
			subApp.render();
		} else {
			super.render();
			cameraButton.render();
			paintButton.render();
		}
	}

	@Override
	public ResourceLocation getIcon() {
		return ICON;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("hasCustomWallpaper")) {
			hasCustomWallpaper = nbt.getBoolean("hasCustomWallpaper");
		}
		if (nbt.contains("wallpaper")) {
			toWallpaper(nbt.getIntArray("wallpaper"));
			if (hasCustomWallpaper)
				phone.setWallpaper(wallpaper);
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putIntArray("wallpaper", fromWallpaper());
		compound.putBoolean("hasCustomWallpaper", hasCustomWallpaper);
		return compound;
	}

	private void toWallpaper(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			int x = i / PhoneUtils.WALLPAPER_HEIGHT;
			int y = i % PhoneUtils.WALLPAPER_HEIGHT;
			wallpaper[x][y] = arr[i];
		}
	}

	private int[] fromWallpaper() {
		int[] arr = new int[PhoneUtils.WALLPAPER_HEIGHT * PhoneUtils.WALLPAPER_WIDTH];
		for (int x = 0; x < PhoneUtils.WALLPAPER_WIDTH; x++) {
			for (int y = 0; y < PhoneUtils.WALLPAPER_HEIGHT; y++) {
				int i = x * PhoneUtils.WALLPAPER_HEIGHT + y;
				arr[i] = wallpaper[x][y];
			}
		}
		return arr;
	}

	private class PaintApp extends App {

		private final ResourceLocation PAINT_BACKGROUND = new ResourceLocation(Main.MODID,
				"textures/gui/wallpaper_app/paint_background.png");

		private final float CANVAS_SIZE = 0.8f;
		private final Color[] colors = new Color[] { Color.BLACK, Color.WHITE, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
				Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW };

		private Color brushColor = Color.BLACK;
		private Button[] colorButtons;
		private Button confirmButton;
		private int confirmMessageTimer;

		public PaintApp(Phone phone) {
			super(phone);

			colorButtons = new Button[colors.length];

			int borderSize = 4;
			float buttonX = PhoneUtils.APP_WIDTH * CANVAS_SIZE + borderSize;
			for (int i = 0; i < colors.length; i++) {
				Color buttonColor = colors[i];
				colorButtons[i] = new Button(new Rectangle(buttonX, i * 16, 16), PhoneUtils.WHITE_PIXEL, phone,
						() -> brushColor = buttonColor, colors[i]);
			}

			confirmButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 16, PhoneUtils.APP_HEIGHT * 0.84f, 32),
					CONFIRM, phone, () -> {
						confirmMessageTimer = 40;
						phone.setWallpaper(wallpaper);
						hasCustomWallpaper = true;
					});
		}

		@Override
		public void tick() {
			super.tick();

			if (phone.isLeftDown())
				paintPixel(phone.getMouseX(), phone.getMouseY());

			for (Button b : colorButtons)
				b.tick();

			confirmButton.tick();

			confirmMessageTimer--;
		}

		private void paintPixel(float mouseX, float mouseY) {
			int x = (int) MathHelper.lerp(mouseX / (CANVAS_SIZE * PhoneUtils.APP_WIDTH), 0, PhoneUtils.WALLPAPER_WIDTH);
			int y = (int) MathHelper.lerp(mouseY / (CANVAS_SIZE * PhoneUtils.APP_HEIGHT), 0,
					PhoneUtils.WALLPAPER_HEIGHT);

			if (x < PhoneUtils.WALLPAPER_WIDTH && y < PhoneUtils.WALLPAPER_HEIGHT && x >= 0 && y >= 0) {
				wallpaper[x][y] = brushColor.getRGB();
			}
		}

		@Override
		public void render() {
			super.render();

			PhoneUtils.drawWallpaper(wallpaper, 0, 0, CANVAS_SIZE * PhoneUtils.APP_WIDTH,
					CANVAS_SIZE * PhoneUtils.APP_HEIGHT);

			for (Button b : colorButtons)
				b.render();

			confirmButton.render();

			if (confirmMessageTimer > 0) {
				PhoneUtils.writeOnPhone(font, "Wallpaper updated!", 3, 3, Color.BLACK, 0.5f, false);
			}
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return PAINT_BACKGROUND;
		}

	}

	private class CameraApp extends App {

		private Button capture;
		private int photoTakenTimer;
		private String photoTakenMessage = "Wallpaper Updated!";

		public CameraApp(Phone phone) {
			super(phone);
			capture = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 16, PhoneUtils.APP_HEIGHT * 0.8f, 32),
					CAPTURE, phone, () -> takePhoto());
		}

		private void takePhoto() {
			photoTakenTimer = 40;
			photoTakenMessage = "Wallpaper Updated!";
			hasCustomWallpaper = true;
			Minecraft mc = Minecraft.getInstance();
			MainWindow window = mc.getMainWindow();

			float windowWidth = window.getScaledWidth();
			float windowHeight = window.getScaledHeight();
			int bufferWidth = window.getFramebufferWidth();
			int bufferHeight = window.getFramebufferHeight();

			NativeImage photo = ScreenShotHelper.createScreenshot(bufferWidth, bufferHeight, mc.getFramebuffer());
			if (photo.getFormat() != NativeImage.PixelFormat.RGBA) {
				photoTakenMessage = "Failed to take photo!";
				return;
			}

			int left = (int) ((windowWidth * 0.5f - PhoneUtils.SCREEN_HORIZONTAL_CENTER_OFFSET) / windowWidth
					* bufferWidth);
			int top = (int) ((windowHeight - PhoneUtils.SCREEN_BOTTON_OFFSET - PhoneUtils.SCREEN_HEIGHT) / windowHeight
					* bufferHeight);
			int right = (int) ((windowWidth * 0.5f + PhoneUtils.SCREEN_HORIZONTAL_CENTER_OFFSET) / windowWidth
					* window.getFramebufferWidth());
			int bottom = (int) ((windowHeight - PhoneUtils.SCREEN_BOTTON_OFFSET) / windowHeight
					* bufferHeight);
			
			int pixelWidth = (int) ((right - left) / (float) PhoneUtils.WALLPAPER_WIDTH);
			int pixelHeight = (int) ((bottom - top) / (float) PhoneUtils.WALLPAPER_HEIGHT);
			
			for (int x = 0; x < PhoneUtils.WALLPAPER_WIDTH; x++) {
				for (int y = 0; y < PhoneUtils.WALLPAPER_HEIGHT; y++) {
					int pixelX = MathHelper.clamp(left + x * pixelWidth, 0, photo.getWidth());
					int pixelY = MathHelper.clamp(top + y * pixelHeight, 0, photo.getHeight());
					wallpaper[x][y] = fromNativeImageColor(photo.getPixelRGBA(pixelX, pixelY));
				}
			}

		}

		private int fromNativeImageColor(int pixelRGBA) {
			int a = NativeImage.getAlpha(pixelRGBA);
			int r = NativeImage.getRed(pixelRGBA);
			int g = NativeImage.getGreen(pixelRGBA);
			int b = NativeImage.getBlue(pixelRGBA);
			return new Color(r, g, b, a).getRGB();
		}

		@Override
		public void tick() {
			super.tick();
			capture.tick();

			photoTakenTimer--;
		}

		@Override
		public void render() {
			super.render();
			capture.render();

			if (photoTakenTimer > 0) {
				PhoneUtils.writeOnPhone(font, photoTakenMessage, 3, 3, Color.WHITE, 0.5f, false);
			}
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return TRANSPARENT;
		}

	}

}
