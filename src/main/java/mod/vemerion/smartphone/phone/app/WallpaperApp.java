package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

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

	private static final ResourceLocation ERASER = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/eraser_button.png");
	private static final ResourceLocation PIPETTE = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/pipette_button.png");
	private static final ResourceLocation BRUSH = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/brush_button.png");
	private static final ResourceLocation COLOR_INDICATOR = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/color_indicator.png");
	private static final ResourceLocation COLOR_INDICATOR_OVERLAY = new ResourceLocation(Main.MODID,
			"textures/gui/wallpaper_app/color_indicator_overlay.png");

	private enum Mode {
		BRUSH, ERASER, PIPETTE
	};

	private Button cameraButton;
	private Button paintButton;
	private App subApp;
	private int[][] wallpaper;
	private boolean hasCustomWallpaper;

	public WallpaperApp(Phone phone) {
		super(phone);
		cameraButton = new Button(new Rectangle(0, 0, PhoneUtils.APP_HEIGHT / 2), () -> CAMERA, phone,
				() -> subApp = new CameraApp(phone));
		paintButton = new Button(new Rectangle(0, PhoneUtils.APP_HEIGHT / 2, PhoneUtils.APP_HEIGHT / 2), () -> PAINT,
				phone, () -> subApp = new PaintApp(phone));

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
	public void render(PoseStack matrix) {
		if (subApp != null) {
			subApp.render(matrix);
		} else {
			super.render(matrix);
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
	public void deserializeNBT(CompoundTag nbt) {
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
	public CompoundTag serializeNBT() {
		var compound = new CompoundTag();
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

		private Color brushColor = Color.BLACK;
		private Button brushButton;
		private Button eraserButton;
		private Button pipetteButton;
		private Mode currentMode;
		private List<Button> colorButtons;

		public PaintApp(Phone phone) {
			super(phone);

			currentMode = Mode.BRUSH;

			colorButtons = new ArrayList<>();

			float startX = 2.5f;
			float startY = PhoneUtils.APP_HEIGHT * 0.825f;
			float buttonWidth = 4;
			int rowCount = (int) ((PhoneUtils.APP_WIDTH - startX * 2) / (buttonWidth + 1));
			int i = 0;
			for (int r = 0; r < 5; r++) {
				for (int g = 0; g < 5; g++) {
					for (int b = 0; b < 5; b++) {
						Color color = new Color(r * 63, g * 63, b * 63);
						colorButtons.add(new Button(
								new Rectangle(startX + (buttonWidth + 1) * (i % rowCount),
										startY + (buttonWidth + 1) * (i / rowCount), buttonWidth),
								() -> PhoneUtils.WHITE_PIXEL, phone, () -> brushColor = color, color));
						i++;
					}
				}
			}

			brushButton = new CanvasButton(new Rectangle(PhoneUtils.APP_WIDTH * CANVAS_SIZE + 4, 0, 16), () -> BRUSH,
					phone, () -> {
						currentMode = Mode.BRUSH;
					}, Mode.BRUSH);

			eraserButton = new CanvasButton(new Rectangle(PhoneUtils.APP_WIDTH * CANVAS_SIZE + 4, 16, 16), () -> ERASER,
					phone, () -> {
						currentMode = Mode.ERASER;
					}, Mode.ERASER);

			pipetteButton = new CanvasButton(new Rectangle(PhoneUtils.APP_WIDTH * CANVAS_SIZE + 4, 32, 16),
					() -> PIPETTE, phone, () -> {
						currentMode = Mode.PIPETTE;
					}, Mode.PIPETTE);

			startup();
		}

		@Override
		public void tick() {
			super.tick();

			brushButton.tick();
			eraserButton.tick();
			pipetteButton.tick();

			for (Button b : colorButtons)
				b.tick();

			if (phone.isLeftDown())
				canvasAction(phone.getMouseX(), phone.getMouseY());
		}

		private void canvasAction(float mouseX, float mouseY) {
			int x = (int) Mth.lerp(mouseX / (CANVAS_SIZE * PhoneUtils.APP_WIDTH), 0, PhoneUtils.WALLPAPER_WIDTH);
			int y = (int) Mth.lerp(mouseY / (CANVAS_SIZE * PhoneUtils.APP_HEIGHT), 0,
					PhoneUtils.WALLPAPER_HEIGHT);

			if (x < PhoneUtils.WALLPAPER_WIDTH && y < PhoneUtils.WALLPAPER_HEIGHT && x >= 0 && y >= 0) {
				if (currentMode == Mode.BRUSH)
					wallpaper[x][y] = brushColor.getRGB();
				else if (currentMode == Mode.ERASER)
					wallpaper[x][y] = Color.WHITE.getRGB();
				else if (currentMode == Mode.PIPETTE)
					brushColor = new Color(wallpaper[x][y], true);
			}
		}

		@Override
		public void render(PoseStack matrix) {
			super.render(matrix);

			brushButton.render();
			eraserButton.render();
			pipetteButton.render();

			for (Button b : colorButtons)
				b.render();

			PhoneUtils.drawWallpaper(wallpaper, 0, 0, CANVAS_SIZE * PhoneUtils.APP_WIDTH,
					CANVAS_SIZE * PhoneUtils.APP_HEIGHT);

			PhoneUtils.drawOnPhone(COLOR_INDICATOR, CANVAS_SIZE * PhoneUtils.APP_WIDTH + 4, PhoneUtils.APP_HEIGHT * 0.74f, 16, 16);
			PhoneUtils.drawOnPhone(COLOR_INDICATOR_OVERLAY, CANVAS_SIZE * PhoneUtils.APP_WIDTH + 4, PhoneUtils.APP_HEIGHT * 0.74f, 16, 16, brushColor);
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return PAINT_BACKGROUND;
		}

		private class CanvasButton extends Button {
			private Mode mode;

			public CanvasButton(Rectangle rectangle, Supplier<ResourceLocation> icon, Phone phone, Runnable r,
					Mode mode) {
				super(rectangle, icon, phone, r);
				this.mode = mode;
			}

			@Override
			protected Color getColor() {
				if (currentMode == mode)
					return HOVER_COLOR;
				else
					return super.getColor();
			}

		}
	}

	private class CameraApp extends App {

		private Button capture;
		private int photoTakenTimer;
		private String photoTakenMessage = "Wallpaper Updated!";
		private int photoCountdown = -1;

		public CameraApp(Phone phone) {
			super(phone);
			capture = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 16, PhoneUtils.APP_HEIGHT * 0.8f, 32),
					() -> CAPTURE, phone, () -> photoCountdown = 3);

			startup();
		}

		private void takePhoto() {
			photoTakenTimer = 40;
			photoTakenMessage = "Wallpaper Updated!";
			hasCustomWallpaper = true;
			var mc = Minecraft.getInstance();
			var window = mc.getWindow();

			float windowWidth = window.getGuiScaledWidth();
			float windowHeight = window.getGuiScaledHeight();
			float bufferWidth = mc.getMainRenderTarget().viewWidth;
			float bufferHeight = mc.getMainRenderTarget().viewHeight;

			var photo = Screenshot.takeScreenshot(mc.getMainRenderTarget());
			if (photo.format() != NativeImage.Format.RGBA) {
				photoTakenMessage = "Failed to take photo!";
				return;
			}

			int left = (int) ((windowWidth * 0.5f - PhoneUtils.SCREEN_HORIZONTAL_CENTER_OFFSET) / windowWidth
					* bufferWidth);
			int top = (int) ((windowHeight - PhoneUtils.SCREEN_BOTTON_OFFSET - PhoneUtils.SCREEN_HEIGHT) / windowHeight
					* bufferHeight);
			int right = (int) ((windowWidth * 0.5f + PhoneUtils.SCREEN_HORIZONTAL_CENTER_OFFSET) / windowWidth
					* bufferWidth);
			int bottom = (int) ((windowHeight - PhoneUtils.SCREEN_BOTTON_OFFSET) / windowHeight * bufferHeight);

			int pixelWidth = (int) ((right - left) / (float) PhoneUtils.WALLPAPER_WIDTH);
			int pixelHeight = (int) ((bottom - top) / (float) PhoneUtils.WALLPAPER_HEIGHT);

			for (int x = 0; x < PhoneUtils.WALLPAPER_WIDTH; x++) {
				for (int y = 0; y < PhoneUtils.WALLPAPER_HEIGHT; y++) {
					int pixelX = Mth.clamp(left + x * pixelWidth, 0, photo.getWidth());
					int pixelY = Mth.clamp(top + y * pixelHeight, 0, photo.getHeight());
					wallpaper[x][y] = fromNativeImageColor(photo.getPixelRGBA(pixelX, pixelY));
				}
			}

			phone.setWallpaper(wallpaper);
			hasCustomWallpaper = true;
		}

		private int fromNativeImageColor(int pixelRGBA) {
			int a = FastColor.ABGR32.m_266503_(pixelRGBA); // alpha
			int r = FastColor.ABGR32.m_266313_(pixelRGBA); // Red
			int g = FastColor.ABGR32.m_266446_(pixelRGBA); // Green
			int b = FastColor.ABGR32.m_266247_(pixelRGBA); // Blue
			return new Color(r, g, b, a).getRGB();
		}

		@Override
		public void tick() {
			super.tick();

			if (photoCountdown-- == 0) {
				takePhoto();
			}

			capture.tick();

			photoTakenTimer--;
		}

		@Override
		public void render(PoseStack matrix) {
			super.render(matrix);
			if (photoCountdown < 0)
				capture.render();

			if (photoTakenTimer > 0) {
				PhoneUtils.writeOnPhone(matrix, font, photoTakenMessage, 3, 3, Color.WHITE, 0.5f, false);
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
