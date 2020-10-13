package mod.vemerion.smartphone.phone.utils;

import java.awt.Color;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.util.ResourceLocation;

public class Button {
	private static final Color HOVER_COLOR = new Color(100, 100, 100);

	private Rectangle rectangle;
	private ResourceLocation icon;
	private Phone phone;
	private Runnable runnable;
	private boolean isLeftDownPrev;
	private Color color = Color.WHITE;

	public Button(Rectangle rectangle, ResourceLocation icon, Phone phone, Runnable runnable) {
		this.rectangle = rectangle;
		this.icon = icon;
		this.phone = phone;
		this.runnable = runnable;
	}

	public Button(Rectangle rectangle, ResourceLocation icon, Phone phone, Runnable runnable, Color color) {
		this(rectangle, icon, phone, runnable);
		this.color = color;
	}

	public void tick() {
		boolean isLeftDown = phone.isLeftDown();
		if (isLeftDown && !isLeftDownPrev && rectangle.contains(phone.getMouseX(), phone.getMouseY())) {
			onPress();
		}
		isLeftDownPrev = isLeftDown;
	}

	public void render() {
		Color c = rectangle.contains(phone.getMouseX(), phone.getMouseY()) ? HOVER_COLOR : color;
		PhoneUtils.drawOnPhone(icon, rectangle.x, rectangle.y, rectangle.width, rectangle.height, c);
	}

	private void onPress() {
		phone.playSound(Main.CLICK_SOUND, 2f);
		runnable.run();
	}
}
