package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import net.minecraft.util.ResourceLocation;

public class VillagerChatApp extends App {

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID,
			"textures/gui/villager_chat_app/icon.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/villager_chat_app/background.png");

	private static final String[] RESPONSES = { "hmmmmmmmmm", "hhhhmmmmmm", "mhmmm",
			"hhhhhhhhhhhhhhhhhhhhmmmmmmmmmmmm", "mmmmmmmhhhmmmmmmmmmm", "hhhhhhhhmmmmmmmmmm" };
	private static final String[] CHARS = { "h", "m" };

	private List<String> chat;
	private String message;

	public VillagerChatApp(Phone phone) {
		super(phone);
		chat = new ArrayList<>();
		message = "";
	}

	@Override
	public void tick() {
		super.tick();
		
		List<Integer> keys = phone.getKeys();
		
		if (keys.size() > 0 && message.length() < 45 && ticksExisted % 3 == 0)
			phone.playSound(Main.WRITE_SOUND, 0.3f);
		
		for (int key : phone.getKeys()) {
			if (message.length() < 45 && key != GLFW.GLFW_KEY_ENTER) {
				message += CHARS[rand.nextInt(CHARS.length)];
			}
		}

		if (phone.isKeyDown(GLFW.GLFW_KEY_ENTER) && !message.isEmpty()) {
			chat.add("P" + message);
			message = "";
		}

		if (rand.nextDouble() < 0.02) {
			chat.add("V" + RESPONSES[rand.nextInt(RESPONSES.length)]);
			phone.playSound(Main.CATCH_APPLE_SOUND, 0.3f);
		}
	}

	@Override
	public void render() {
		super.render();
		printMsg(message, 2, 175, new Color(0, 180, 255), 15);

		float y = 150;
		for (int i = chat.size() - 1; i >= 0; i--) {
			boolean fromPlayer = chat.get(i).startsWith("P");
			String msg = chat.get(i).substring(1);
			y -= Math.ceil(msg.length() / (float) 8) * 8.5f + 10;
			if (y < 2)
				break;
			printMsg(msg, fromPlayer ? 2 : 50, y, fromPlayer ? new Color(0, 180, 255) : new Color(0, 255, 180), 8);

		}
	}

	private void printMsg(String msg, float x, float y, Color color, int maxLength) {
		double count = Math.ceil(msg.length() / (float) maxLength) + 0.1;
		for (int i = 0; i < count; i++) {
			PhoneUtils.writeOnPhone(font, msg.substring(0, Math.min(maxLength, msg.length())), x, y + i * 8.5f, color, 0.75f, false);
			msg = msg.substring(Math.min(maxLength, msg.length()));
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

}
