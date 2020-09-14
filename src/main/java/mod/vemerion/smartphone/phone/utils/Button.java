package mod.vemerion.smartphone.phone.utils;

import java.awt.Color;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.util.ResourceLocation;

public class Button {
	private Rectangle rectangle;
	private ResourceLocation icon;
	private Phone phone;
	private Runnable runnable;
	private boolean isLeftDownPrev;

	public Button(Rectangle rectangle, ResourceLocation icon, Phone phone, Runnable runnable) {
		this.rectangle = rectangle;
		this.icon = icon;
		this.phone = phone;
		this.runnable = runnable;
	}

	public void tick() {
		boolean isLeftDown = phone.isLeftDown();
		if (isLeftDown && !isLeftDownPrev && rectangle.contains(phone.getMouseX(), phone.getMouseY())) {
			onPress();
		}
		isLeftDownPrev = isLeftDown;
	}

	public void render() {
		Color color = rectangle.contains(phone.getMouseX(), phone.getMouseY()) ? new Color(100, 100, 100)
				: new Color(255, 255, 255);
		PhoneUtils.drawOnPhone(icon, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color);
	}

	private void onPress() {
		phone.playSound(Main.CLICK_SOUND, 2f);
		runnable.run();
	}
}
