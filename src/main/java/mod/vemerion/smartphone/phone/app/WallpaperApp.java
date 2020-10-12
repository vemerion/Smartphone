package mod.vemerion.smartphone.phone.app;

import java.awt.Color;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.util.ResourceLocation;

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

	private Button cameraButton;
	private Button paintButton;
	private App subApp;

	public WallpaperApp(Phone phone) {
		super(phone);
		cameraButton = new Button(new Rectangle(0, 0, PhoneUtils.APP_HEIGHT / 2), CAMERA, phone,
				() -> subApp = new CameraApp(phone));
		paintButton = new Button(new Rectangle(0, PhoneUtils.APP_HEIGHT / 2, PhoneUtils.APP_HEIGHT / 2), PAINT, phone,
				() -> subApp = new PaintApp(phone));
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

	private class PaintApp extends App {

		public PaintApp(Phone phone) {
			super(phone);
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return BACKGROUND;
		}

	}

	private class CameraApp extends App {

		private Button capture;
		private int photoTakenTimer;

		public CameraApp(Phone phone) {
			super(phone);
			capture = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 16, PhoneUtils.APP_HEIGHT * 0.8f, 32),
					CAPTURE, phone, () -> photoTakenTimer = 40);
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
				PhoneUtils.writeOnPhone("Wallpaper updated!", 3, 3, Color.WHITE, 0.5f);
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
