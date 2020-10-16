package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.ICommunicator;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;

public class MessageApp extends App implements ICommunicator {

	private static final float HEAD_SIZE = 1 / 8f;

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/message_app/icon.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation ADD_CONTACT = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/add_contact.png");
	private static final ResourceLocation LEFT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/left_button.png");
	private static final ResourceLocation RIGHT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/right_button.png");

	private static final int ADD_CONTACT_BUTTON_X = 2;
	private static final int ADD_CONTACT_BUTTON_SIZE = 30;
	private static final int ADD_CONTACT_TEXT_WIDTH = (int) (PhoneUtils.APP_WIDTH - ADD_CONTACT_BUTTON_SIZE
			- ADD_CONTACT_BUTTON_X);

	private static final int CONTACT_BUTTON_SIZE = 25;
	private static final int CONTACT_BUTTON_BORDER = 1;
	private static final int CONTACT_BUTTONS_PER_PAGE = 5;

	private static final ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");

	private List<ContactInfo> contacts;
	private Button addContactButton;
	private String addContactText;
	private String toast;
	private int toastTimer;
	private Button leftButton;
	private Button rightButton;
	private int page;

	public MessageApp(Phone phone) {
		super(phone);

		addContactText = "";
		toast = "";

//		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> skins = phone.getMinecraft().getSkinManager()
//		.loadSkinFromCache(new GameProfile(null, "dev"));
//ResourceLocation location = phone.getMinecraft().getSkinManager()
//		.loadSkin(skins.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
		contacts = new ArrayList<>();

		addContactButton = new Button(new Rectangle(ADD_CONTACT_BUTTON_X,
				PhoneUtils.APP_HEIGHT - ADD_CONTACT_BUTTON_SIZE - 5, ADD_CONTACT_BUTTON_SIZE), ADD_CONTACT, phone,
				() -> {
					if (!addContactText.isEmpty()) {
						sendAddContactMessage(addContactText);
						addContactText = "";
					}
				});

		rightButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 + 10, PhoneUtils.APP_HEIGHT * 0.67f, 20),
				RIGHT_BUTTON, phone, () -> {
					if ((page + 1) * CONTACT_BUTTONS_PER_PAGE < contacts.size())
						page++;
				});
		leftButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 30, PhoneUtils.APP_HEIGHT * 0.67f, 20),
				LEFT_BUTTON, phone, () -> {
					if (page > 0)
						page--;
				});

		phone.addCommunicator(this);
	}

	@Override
	public void tick() {
		super.tick();

		int start = page * CONTACT_BUTTONS_PER_PAGE;
		for (int i = start; i < Math.min(start + 5, contacts.size()); i++) {
			contacts.get(i).tickButton();
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
		leftButton.tick();
		rightButton.tick();
		toastTimer--;
	}

	@Override
	public void render() {
		super.render();

		int start = page * CONTACT_BUTTONS_PER_PAGE;
		for (int i = start; i < Math.min(start + 5, contacts.size()); i++) {
			contacts.get(i).renderButton();
		}
		
		leftButton.render();
		rightButton.render();

		addContactButton.render();
		int textX = ADD_CONTACT_BUTTON_X + ADD_CONTACT_BUTTON_SIZE + 1;
		PhoneUtils.writeOnPhoneTrim(font, addContactText, textX, PhoneUtils.APP_HEIGHT - 20, Color.BLACK, 0.5f,
				ADD_CONTACT_TEXT_WIDTH, true);

		if (toastTimer > 0)
			PhoneUtils.writeOnPhone(font, toast, 5, PhoneUtils.APP_HEIGHT - ADD_CONTACT_BUTTON_SIZE - 15, Color.BLACK,
					0.75f);
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
	public void recieveAddContactAck(UUID uuid, String name, boolean success) {
		if (success) {
			contacts.add(new ContactInfo(phone, uuid, name, new ArrayList<>(), new ArrayList<>()));
			toast = "Contact added!";
			toastTimer = 40;
		} else {
			toast = "Invalid contact!";
			toastTimer = 40;
		}
	}

	private class ContactInfo extends App {
		private UUID uuid;
		private String name;
		private List<String> recievedText;
		private List<String> sentText;
		private Button button;
		private float y;

		public ContactInfo(Phone phone, UUID uuid, String name, List<String> recievedText, List<String> sentText) {
			super(phone);
			this.uuid = uuid;
			this.name = name;
			this.recievedText = recievedText;
			this.sentText = sentText;
			this.y = contacts.size() % CONTACT_BUTTONS_PER_PAGE * (CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER)
					+ CONTACT_BUTTON_BORDER;
			this.button = new ContactButton(new Rectangle(CONTACT_BUTTON_BORDER, y, CONTACT_BUTTON_SIZE), STEVE, phone,
					() -> {
					});

			startup();
		}

		private void tickButton() {
			button.tick();
		}

		private void renderButton() {
			button.render();

			PhoneUtils.writeOnPhoneTrim(font, name, CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER + 1, y + 1, Color.BLACK,
					0.8f, PhoneUtils.APP_WIDTH - CONTACT_BUTTON_SIZE - 1, false);
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

	private class ContactButton extends Button {

		public ContactButton(Rectangle rectangle, ResourceLocation icon, Phone phone, Runnable runnable) {
			super(rectangle, icon != null ? icon : STEVE, phone, runnable,
					new Rectangle(HEAD_SIZE, HEAD_SIZE, HEAD_SIZE));
		}

	}

}
