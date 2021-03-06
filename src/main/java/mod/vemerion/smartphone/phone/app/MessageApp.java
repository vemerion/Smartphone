package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.ICommunicator;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.common.util.Constants;

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
				if (SharedConstants.isAllowedCharacter(c) && addContactText.length() < 20) {
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
	public void render(MatrixStack matrix) {
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
					ADD_CONTACT_TEXT_WIDTH + 2, font.FONT_HEIGHT, 0, 0, 1, 1);

			PhoneUtils.writeOnPhoneTrim(matrix, font, addContactText, textX, PhoneUtils.APP_HEIGHT - 20, Color.BLACK, 0.5f,
					ADD_CONTACT_TEXT_WIDTH, true, false);

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
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		ListNBT infos = new ListNBT();
		for (ContactInfo contact : contacts)
			infos.add(contact.serializeNBT());
		compound.put("contacts", infos);
		compound.putBoolean("hasUnreadMessages", hasUnreadMessages);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT infos = nbt.getList("contacts", Constants.NBT.TAG_COMPOUND);
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

		public ContactInfo(Phone phone, CompoundNBT compound) {
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

		private void renderButton(MatrixStack matrix) {
			button.render();

			PhoneUtils.writeOnPhoneTrim(matrix, font, name, CONTACT_BUTTON_SIZE + CONTACT_BUTTON_BORDER + 1, y + 1, Color.BLACK,
					0.8f, PhoneUtils.APP_WIDTH - CONTACT_BUTTON_SIZE - 1, false, false);

			if (hasUnreadMessages)
				button.renderAlert();
		}

		@Override
		public void tick() {
			super.tick();
			backButton.tick();

			for (char c : phone.getCharsTyped()) {
				if (message.length() < 55 && SharedConstants.isAllowedCharacter(c)) {
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
		public void render(MatrixStack matrix) {
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

			PhoneUtils.writeOnPhoneTrim(matrix, font, name, 25, 6, Color.BLACK, 1, PhoneUtils.APP_WIDTH - 25, false, false);

			PhoneUtils.writeOnPhoneWrap(matrix, font, message, 1, MESSAGE_LINE + 8, Color.BLACK, 0.8f, PhoneUtils.APP_WIDTH - 2,
					false);
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
		public CompoundNBT serializeNBT() {
			CompoundNBT compound = new CompoundNBT();
			compound.putUniqueId("contactId", uuid);
			compound.putString("contactName", name);
			ListNBT msgs = new ListNBT();
			for (int i = Math.max(0, messages.size() - 10); i < messages.size(); i++) {
				msgs.add(StringNBT.valueOf(messages.get(i)));
			}
			compound.put("messages", msgs);
			compound.putBoolean("hasUnreadMessages", hasUnreadMessages);
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			uuid = nbt.getUniqueId("contactId");
			name = nbt.getString("contactName");
			ListNBT msgs = nbt.getList("messages", Constants.NBT.TAG_STRING);
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
			SkinManager skinManager = phone.getMinecraft().getSkinManager();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> skins = skinManager.loadSkinFromCache(profile);
			if (skins.containsKey(MinecraftProfileTexture.Type.SKIN))
				icon = () -> skinManager.loadSkin(skins.get(MinecraftProfileTexture.Type.SKIN),
						MinecraftProfileTexture.Type.SKIN);
			else
				skinManager.loadProfileTextures(profile, (type, location, texture) -> {
					if (type == MinecraftProfileTexture.Type.SKIN)
						icon = () -> location;
				}, true);
		}

		private void renderAlert() {
			PhoneUtils.drawOnPhone(ALERT, rectangle.x, rectangle.y, rectangle.width, rectangle.height, 0, 0, 1, 1);
		}

	}
}
