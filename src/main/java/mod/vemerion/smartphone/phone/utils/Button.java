package mod.vemerion.smartphone.phone.utils;

import java.awt.Color;
import java.util.function.Supplier;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.util.ResourceLocation;

public class Button {
	protected static final Color HOVER_COLOR = new Color(100, 100, 100);

	protected Rectangle rectangle;
	protected Supplier<ResourceLocation> icon;
	protected Phone phone;
	private Runnable runnable;
	private boolean isLeftDownPrev;
	private Color color = Color.WHITE;
	protected Rectangle texBounds;

	public Button(Rectangle rectangle, Supplier<ResourceLocation> icon, Phone phone, Runnable runnable, Rectangle texBounds) {
		this.rectangle = rectangle;
		this.icon = icon;
		this.phone = phone;
		this.runnable = runnable;
		this.texBounds = texBounds;
	}

	public Button(Rectangle rectangle, Supplier<ResourceLocation> icon, Phone phone, Runnable runnable) {
		this(rectangle, icon, phone, runnable, new Rectangle(0, 0, 1));
	}

	public Button(Rectangle rectangle, Supplier<ResourceLocation> icon, Phone phone, Runnable runnable, Color color) {
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
	
	protected Color getColor() {
		return color;
	}

	public void render() {
		Color c = rectangle.contains(phone.getMouseX(), phone.getMouseY()) ? HOVER_COLOR : getColor();
		PhoneUtils.drawOnPhone(icon.get(), rectangle.x, rectangle.y, rectangle.width, rectangle.height, texBounds.x,
				texBounds.y, texBounds.width, texBounds.height, c);
	}

	private void onPress() {
		phone.playSound(Main.CLICK_SOUND, 2f);
		runnable.run();
	}
}
