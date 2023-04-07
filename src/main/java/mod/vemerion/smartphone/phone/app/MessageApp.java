package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.ICommunicator;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class MessageApp extends App implements ICommunicator {

	private static final float HEAD_SIZE = 1 / 8f;

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/message_app/icon.png");
	private static final ResourceLocation ICON_ALERT = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/icon_alert.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation ADD_CONTACT = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/add_contact.png");
	private static final ResourceLocation LEFT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/left_button.png");
	private static final ResourceLocation RIGHT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/right_button.png");
	private static final ResourceLocation ALERT = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/alert.png");
	private static final ResourceLocation ADD_CONTACT_TEXT_FIELD = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/add_contact_text_field.png");
	private static final ResourceLocation MESSAGE_BUBBLE = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/message_bubble.png");

	private static final Color YOU_COLOR = new Color(85, 255, 255);
	private static final Color OTHER_COLOR = new Color(150, 255, 120);

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
	private App subApp;
	private boolean hasUnreadMessages;

	public MessageApp(Phone phone) {
		super(phone);

		addContactText = "";
		toast = "";

		contacts = new ArrayList<>();

		addContactButton = new Button(new Rectangle(ADD_CONTACT_BUTTON_X,
				PhoneUtils.APP_HEIGHT - ADD_CONTACT_BUTTON_SIZE - 5, ADD_CONTACT_BUTTON_SIZE), () -> ADD_CONTACT, phone,
				() -> {
					if (!addContactText.isEmpty()) {
						sendAddContactMessage(addContactText);
						addContactText = "";
					}
				});

		rightButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 + 10, PhoneUtils.APP_HEIGHT * 0.67f, 20),
				() -> RIGHT_BUTTON, phone, () -> {
					if ((page + 1) * CONTACT_BUTTONS_PER_PAGE < contacts.size())
						page++;
				});
		leftButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 30, PhoneUtils.APP_HEIGHT * 0.67f, 20),
				() -> LEFT_BUTTON, phone, () -> {
					if (page > 0)
						page--;
				});

		phone.addCommunicator(this);
	}

	@Override
	public void resume() {
		super.resume();
		hasUnreadMessages = false;
	}

	@Override
	public void suspend() {
		super.suspend();
		subApp = null;
	}

	@Override
	public void tick() {
		super.tick();

		if (subApp != null) {
			subApp.tick();
		} else {
			int start = page * CONTACT_BUTTONS_PER_PAGE;
			for (int i = start; i < Math.min(start + 5, contacts.size()); i++) {
				contacts.get(i).tickButton();
			}

			for (char c : phone.getCharsTyped()) {
				if (SharedConstants.isAllowedChatCharacter(c) && addContactText.length() < 20) {
					addContactText += Character.toString(c);
				}
			}

			if (phone.isKeyDown(GLFW.GLFW_KEY_BACKSPACE) && !addContactText.isEmpty()) {
				addContactText = addContactText.substring(0, addContactText.length() - 1);
			}

			if (phone.isKeyDown(GLFW.GLFW_KEY_ENTER) && !addContactText.isEmpty()) {
				sendAddContactMessage(addContactText);
				addContactText = "";
			}

			addContactButton.tick();
			leftButton.tick();
			rightButton.tick();
			toastTimer--;
		}
	}

	@Override
	public void render(PoseStack matrix) {
		super.render(matrix);

		if (subApp != null) {
			subApp.render(matrix);
		} else {
			int start = page * CONTACT_BUTTONS_PER_PAGE;
			for (int i = start; i < Math.min(start + 5, contacts.size()); i++) {
				contacts.get(i).renderButton(matrix);
			}

			leftButton.render();
			rightButton.render();

			addContactButton.render();
			int textX = ADD_CONTACT_BUTTON_X + ADD_CONTACT_BUTTON_SIZE + 1;
			PhoneUtils.drawOnPhone(ADD_CONTACT_TEXT_FIELD, textX - 2, PhoneUtils.APP_HEIGHT - 20 - 2,
					ADD_CONTACT_TEXT_WIDTH + 2, font.lineHeight, 0, 0, 1, 1);

			PhoneUtils.writeOnPhoneTrim(matrix, font, addContactText, textX, PhoneUtils.APP_HEIGHT - 20, Color.BLACK,
					0.5f, ADD_CONTACT_TEXT_WIDTH, true, false);

			if (toastTimer > 0)
				PhoneUtils.writeOnPhone(matrix, font, toast, PhoneUtils.APP_WIDTH / 2,
						PhoneUtils.APP_HEIGHT - ADD_CONTACT_BUTTON_SIZE - 15, Color.BLACK, 0.75f, true);
		}
	}

	@Override
	public ResourceLocation getIcon() {
		return hasUnreadMessages ? ICON_ALERT : ICON;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

	@Override
	public void recieveAddContactAck(UUID uuid, String name, boolean success) {
		if (success && !contacts.stream().anyMatch(contact -> contact.uuid.equals(uuid))) {
			contacts.add(new ContactInfo(phone, uuid, name, new ArrayList<>()));
			toast = "Contact added!";
			toastTimer = 40;
		} else {
			toast = "Invalid contact!";
			toastTimer = 40;
		}
	}

	@Override
	public void recieveTextMessage(UUID source, String sourceName, String message) {
		// Alert
		if (!phone.isAppActive(this))
			hasUnreadMessages = true;

		for (ContactInfo contact : contacts) {
			if (contact.uuid.equals(source)) {
				if (!sourceName.isEmpty())
					contact.name = sourceName;
				contact.addMessage(message);
				return;
			}
		}

		// Add new contact
		List<String> messages = new ArrayList<>();
		messages.add(message);
		contacts.add(new ContactInfo(phone, source, !sourceName.isEmpty() ? sourceName : source.toString(), messages));
	}

	@Override
	public CompoundTag serializeNBT() {
		var compound = new CompoundTag();
		var infos = new ListTag();
		for (ContactInfo contact : contacts)
			infos.add(contact.serializeNBT());
		compound.put("contacts", infos);
		compound.putBoolean("hasUnreadMessages", hasUnreadMessages);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		var infos = nbt.getList("contacts", Tag.TAG_COMPOUND);
		for (int i = 0; i < infos.size(); i++) {
			ContactInfo contact = new ContactInfo(phone, infos.getCompound(i));
			contacts.add(contact);
		}
		if (nbt.contains("hasUnreadMessages"))
			hasUnreadMessages = nbt.getBoolean("hasUnreadMessages");
	}

	private static final ResourceLocation CONVERSATION_BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/message_app/conversation_background.png");
	private static final int MESSAGE_LINE = 155;
	private static final float MESSAGE_WIDTH = PhoneUtils.APP_WIDTH * 0.45f;

	private class ContactInfo extends App {
		private UUID uuid;
		private String name;
		private List<String> messages;
		private ContactButton button;
		private Button backButton;
		private float y;
		private String message = "";
		private boolean hasUnreadMessages = true;

		public ContactInfo(Phone phone, CompoundTag compound) {
			super(phone);
			this.y = contacts.size() % CONTACT_BUTTONS_PER_PAGE * (CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER)
					+ CONTACT_BUTTON_BORDER;
			deserializeNBT(compound);
			startup();

			this.backButton = new Button(new Rectangle(2, 2, 20), () -> LEFT_BUTTON, phone, () -> {
				subApp = null;
			});
		}

		public ContactInfo(Phone phone, UUID uuid, String name, List<String> messages) {
			super(phone);
			this.uuid = uuid;
			this.name = name;
			this.messages = messages;
			this.y = contacts.size() % CONTACT_BUTTONS_PER_PAGE * (CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER)
					+ CONTACT_BUTTON_BORDER;
			this.button = new ContactButton(new Rectangle(CONTACT_BUTTON_BORDER, y, CONTACT_BUTTON_SIZE),
					new GameProfile(uuid, null), phone, () -> {
						subApp = this;
						hasUnreadMessages = false;
					});

			this.backButton = new Button(new Rectangle(2, 2, 20), () -> LEFT_BUTTON, phone, () -> {
				subApp = null;
			});
			startup();
		}

		public void addMessage(String msg) {
			messages.add(msg);
			if (subApp != this)
				hasUnreadMessages = true;
		}

		private void tickButton() {
			button.tick();
		}

		private void renderButton(PoseStack matrix) {
			button.render();

			PhoneUtils.writeOnPhoneTrim(matrix, font, name, CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER + 1, y + 1,
					Color.BLACK, 0.8f, PhoneUtils.APP_WIDTH - CONTACT_BUTTON_SIZE - 1, false, false);

			if (hasUnreadMessages)
				button.renderAlert();
		}

		@Override
		public void tick() {
			super.tick();
			backButton.tick();

			for (char c : phone.getCharsTyped()) {
				if (message.length() < 55 && SharedConstants.isAllowedChatCharacter(c)) {
					message += Character.toString(c);
				}
			}

			if (phone.isKeyDown(GLFW.GLFW_KEY_BACKSPACE) && !message.isEmpty()) {
				message = message.substring(0, message.length() - 1);
			}

			if (phone.isKeyDown(GLFW.GLFW_KEY_ENTER) && !message.isEmpty()) {
				messages.add("you:" + message);
				sendTextMessage(uuid, message);
				message = "";
			}
		}

		@Override
		public void render(PoseStack matrix) {
			super.render(matrix);
			backButton.render();

			float y = MESSAGE_LINE - 2;
			for (int i = messages.size() - 1; i >= 0; i--) {
				String m = messages.get(i);
				boolean fromYou = m.startsWith("you:");
				m = fromYou ? m.substring(4) : m;
				float x = fromYou ? PhoneUtils.APP_WIDTH / 2 + 1 : 4;
				int height = PhoneUtils.textHeight(font, m, 0.5f, MESSAGE_WIDTH);
				y -= 6 + height;

				if (y < 33)
					break;

				PhoneUtils.drawOnPhone(MESSAGE_BUBBLE, x - 2, y - 2, MESSAGE_WIDTH + 4, height + 4,
						fromYou ? YOU_COLOR : OTHER_COLOR);
				PhoneUtils.writeOnPhoneWrap(matrix, font, m, x, y, Color.BLACK, 0.5f, MESSAGE_WIDTH, false);
			}

			PhoneUtils.writeOnPhoneTrim(matrix, font, name, 25, 6, Color.BLACK, 1, PhoneUtils.APP_WIDTH - 25, false,
					false);

			PhoneUtils.writeOnPhoneWrap(matrix, font, message, 1, MESSAGE_LINE + 8, Color.BLACK, 0.8f,
					PhoneUtils.APP_WIDTH - 2, false);
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return CONVERSATION_BACKGROUND;
		}

		@Override
		public CompoundTag serializeNBT() {
			var compound = new CompoundTag();
			compound.putUUID("contactId", uuid);
			compound.putString("contactName", name);
			var msgs = new ListTag();
			for (int i = Math.max(0, messages.size() - 10); i < messages.size(); i++) {
				msgs.add(StringTag.valueOf(messages.get(i)));
			}
			compound.put("messages", msgs);
			compound.putBoolean("hasUnreadMessages", hasUnreadMessages);
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			uuid = nbt.getUUID("contactId");
			name = nbt.getString("contactName");
			var msgs = nbt.getList("messages", Tag.TAG_STRING);
			messages = new ArrayList<>();
			for (int i = 0; i < msgs.size(); i++) {
				messages.add(msgs.getString(i));

			}
			if (nbt.contains("hasUnreadMessages"))
				hasUnreadMessages = nbt.getBoolean("hasUnreadMessages");

			this.button = new ContactButton(new Rectangle(CONTACT_BUTTON_BORDER, y, CONTACT_BUTTON_SIZE),
					new GameProfile(uuid, null), phone, () -> {
						subApp = this;
						hasUnreadMessages = false;
					});
		}

	}

	private class ContactButton extends Button {

		public ContactButton(Rectangle rectangle, GameProfile profile, Phone phone, Runnable runnable) {
			super(rectangle, () -> STEVE, phone, runnable, new Rectangle(HEAD_SIZE, HEAD_SIZE, HEAD_SIZE));
			loadIcon(profile);
		}

		private void loadIcon(GameProfile profile) {
			if (profile.getId() == null)
				return;
			var skinManager = phone.getMinecraft().getSkinManager();
			icon = () -> skinManager.getInsecureSkinLocation(profile);
		}

		private void renderAlert() {
			PhoneUtils.drawOnPhone(ALERT, rectangle.x, rectangle.y, rectangle.width, rectangle.height, 0, 0, 1, 1);
		}

	}
}
