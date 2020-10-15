package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;

public class MessageApp extends App {

	private static final float HEAD_SIZE = 1 / 8f;

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/message_app/icon.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation ADD_CONTACT = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/add_contact.png");

	private static final int ADD_CONTACT_BUTTON_X = 2;
	private static final int ADD_CONTACT_BUTTON_SIZE = 30;
	private static final int ADD_CONTACT_TEXT_WIDTH = (int) (PhoneUtils.APP_WIDTH - ADD_CONTACT_BUTTON_SIZE
			- ADD_CONTACT_BUTTON_X);

	private static final ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");

	private List<Button> contacts;
	private Button addContactButton;
	private String addContactText;

	public MessageApp(Phone phone) {
		super(phone);

		addContactText = "";
	}

	@Override
	public void startup() {
		super.startup();
//		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> skins = phone.getMinecraft().getSkinManager()
//				.loadSkinFromCache(new GameProfile(null, "dev"));
//		ResourceLocation location = phone.getMinecraft().getSkinManager()
//				.loadSkin(skins.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
		contacts = new ArrayList<>();
		contacts.add(new ContactButton(new Rectangle(0, 0, 20, 20), STEVE, phone, () -> {

		}));

		addContactButton = new Button(new Rectangle(ADD_CONTACT_BUTTON_X,
				PhoneUtils.APP_HEIGHT - ADD_CONTACT_BUTTON_SIZE - 5, ADD_CONTACT_BUTTON_SIZE), ADD_CONTACT, phone,
				() -> {

				});
	}

	@Override
	public void tick() {
		super.tick();

		for (Button contact : contacts) {
			contact.tick();
		}

		for (char c : phone.getCharsTyped()) {
			if (SharedConstants.isAllowedCharacter(c)) {
				addContactText += Character.toString(c);
			}
		}

		if (phone.isKeyDown(GLFW.GLFW_KEY_BACKSPACE) && !addContactText.isEmpty()) {
			addContactText = addContactText.substring(0, addContactText.length() - 1);
		}

		addContactButton.tick();
	}

	@Override
	public void render() {
		super.render();

		for (Button contact : contacts) {
			contact.render();
		}

		addContactButton.render();
		int textX = ADD_CONTACT_BUTTON_X + ADD_CONTACT_BUTTON_SIZE + 1;
		PhoneUtils.writeOnPhoneTrim(font, addContactText, textX, PhoneUtils.APP_HEIGHT - 20, Color.BLACK, 0.5f,
				ADD_CONTACT_TEXT_WIDTH, true);
	}

	@Override
	public ResourceLocation getIcon() {
		return ICON;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

	private class ContactButton extends Button {

		public ContactButton(Rectangle rectangle, ResourceLocation icon, Phone phone, Runnable runnable) {
			super(rectangle, icon != null ? icon : STEVE, phone, runnable,
					new Rectangle(HEAD_SIZE, HEAD_SIZE, HEAD_SIZE));
		}

	}

}
