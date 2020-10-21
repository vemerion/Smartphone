package mod.vemerion.smartphone.phone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.network.SavePhoneStateMessage;
import mod.vemerion.smartphone.phone.app.App;
import mod.vemerion.smartphone.phone.app.CatchAppleApp;
import mod.vemerion.smartphone.phone.app.JukeboxApp;
import mod.vemerion.smartphone.phone.app.MapApp;
import mod.vemerion.smartphone.phone.app.MessageApp;
import mod.vemerion.smartphone.phone.app.RunnerApp;
import mod.vemerion.smartphone.phone.app.SuggestionApp;
import mod.vemerion.smartphone.phone.app.VillagerChatApp;
import mod.vemerion.smartphone.phone.app.WallpaperApp;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;

public class Phone extends Screen implements INBTSerializable<CompoundNBT>, ICommunicator {
	private static final ResourceLocation PHONE_TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/smartphone.png");
	private static final ResourceLocation PHONE_BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/phone_background.png");

	private static final ResourceLocation HOME_BUTTON_TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/home_button.png");
	private static final ResourceLocation SHUTDOWN_BUTTON_TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/shutdown_button.png");
	private static final Rectangle HOME_BUTTON = new Rectangle(0, PhoneUtils.APP_HEIGHT,
			(PhoneUtils.APP_WIDTH - 30) / 2, (PhoneUtils.APP_WIDTH - 30) / 4);
	private static final Rectangle SHUTDOWN_BUTTON = new Rectangle((PhoneUtils.APP_WIDTH + 30) / 2,
			PhoneUtils.APP_HEIGHT, (PhoneUtils.APP_WIDTH - 30) / 2, (PhoneUtils.APP_WIDTH - 30) / 4);

	private static final float BUTTON_SIZE = PhoneUtils.APP_WIDTH / 3;

	private List<App> apps;
	private List<Button> appButtons;
	private App activeApp;
	private List<Integer> mouseClicked;
	private Set<Integer> keysPressed;
	private Set<Character> charsTyped;
	private Button homeButton;
	private Button shutdownButton;
	private int[][] wallpaper;
	private List<ICommunicator> communicators;

	public Phone() {
		super(new StringTextComponent(""));

		communicators = new ArrayList<>();
		
		mouseClicked = new ArrayList<>();
		keysPressed = new HashSet<>();
		charsTyped = new HashSet<>();
		apps = new ArrayList<>();
		apps.add(new CatchAppleApp(this));
		apps.add(new JukeboxApp(this));
		apps.add(new VillagerChatApp(this));
		apps.add(new RunnerApp(this));
		apps.add(new SuggestionApp(this));
		apps.add(new WallpaperApp(this));
		apps.add(new MapApp(this));
		apps.add(new MessageApp(this));
		
		// App button
		appButtons = new ArrayList<>();
		for (int i = 0; i < apps.size(); i++) {
			float x = (i % 3) * BUTTON_SIZE + 1;
			float y = (i / 3) * BUTTON_SIZE;
			final App app = apps.get(i);
			Rectangle rectangle = new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE);
			appButtons.add(new Button(rectangle, () -> app.getIcon(), this, () -> {
				activeApp = app;
				app.resume();
			}));
		}

		homeButton = new Button(HOME_BUTTON, () -> HOME_BUTTON_TEXTURE, this, () -> {
			if (activeApp != null)
				activeApp.suspend();
			activeApp = null;
		});
		shutdownButton = new Button(SHUTDOWN_BUTTON, () -> SHUTDOWN_BUTTON_TEXTURE, this, () -> closeScreen());
	}
	
	@Override
	protected void init() {
		super.init();
		
		for (App app : apps) {
			app.startup();
		}
	}
	
	public void setWallpaper(int[][] wallpaper) {
		this.wallpaper = wallpaper;
	}

	public void playSound(SoundEvent sound, float volume) {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		Minecraft.getInstance().world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), sound,
				SoundCategory.PLAYERS, volume, 0.8f + player.getRNG().nextFloat() * 0.4f);
	}

	private boolean atHomeScreen() {
		return activeApp == null;
	}
	
	public boolean isAppActive(App app) {
		return activeApp == app;
	}

	@Override
	public void onClose() {
		if (activeApp != null)
			activeApp.suspend();
		
		for (App app : apps) {
			app.shutdown();
		}
				
		Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new SavePhoneStateMessage(serializeNBT()));
		
		super.onClose();
	}

	@Override
	public void tick() {
		if (atHomeScreen()) {
			for (Button b : appButtons) {
				b.tick();
			}
		} else {
			activeApp.tick();
		}
		homeButton.tick();
		shutdownButton.tick();
		
		charsTyped.clear();
	}
	
	public Set<Character> getCharsTyped() {
		return charsTyped;
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		charsTyped.add(p_charTyped_1_);
		return super.charTyped(p_charTyped_1_, p_charTyped_2_);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		keysPressed.add(keyCode);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		keysPressed.remove((Integer) keyCode);
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	public boolean isKeyDown(int keyCode) {
		return keysPressed.contains(keyCode);
	}

	public List<Integer> getKeys() {
		return new ArrayList<Integer>(keysPressed);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int code) {
		mouseClicked.add(code);
		return super.mouseClicked(mouseX, mouseY, code);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int code) {
		mouseClicked.remove((Integer) code);
		return super.mouseReleased(mouseX, mouseY, code);
	}

	public boolean isLeftDown() {
		return mouseClicked.contains(0);
	}

	public boolean isRightDown() {
		return mouseClicked.contains(1);
	}

	public float getMouseX() {
		Minecraft mc = Minecraft.getInstance();
		MainWindow window = mc.getMainWindow();

		float x = (float) mc.mouseHelper.getMouseX() * (float) window.getScaledWidth() / (float) window.getWidth();
		float center = Minecraft.getInstance().getMainWindow().getScaledWidth() / 2;
		float left = center - PhoneUtils.SCREEN_HORIZONTAL_CENTER_OFFSET;
		return ((x - left) / PhoneUtils.SCREEN_WIDTH) * PhoneUtils.APP_WIDTH;
	}

	public float getMouseY() {
		Minecraft mc = Minecraft.getInstance();
		MainWindow window = mc.getMainWindow();

		float y = (float) mc.mouseHelper.getMouseY() * (float) window.getScaledHeight() / (float) window.getHeight();
		float bottom = Minecraft.getInstance().getMainWindow().getScaledHeight();
		float top = bottom - PhoneUtils.SCREEN_BOTTON_OFFSET - PhoneUtils.SCREEN_HEIGHT;
		return ((y - top) / PhoneUtils.SCREEN_HEIGHT) * PhoneUtils.APP_HEIGHT;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		render(matrix);
	}

	private void render(MatrixStack matrix) {
		MainWindow window = Minecraft.getInstance().getMainWindow();
		float windowWidth = window.getScaledWidth();
		float windowHeight = window.getScaledHeight();


		if (atHomeScreen()) {
			// Draw phone background
			drawBackground();
			
			// Buttons
			for (Button button : appButtons) {
				button.render();
			}
		} else {
			activeApp.render(matrix);
		}

		// Draw Phone
		PhoneUtils.draw(PHONE_TEXTURE, windowWidth / 2 - PhoneUtils.PHONE_HORIZONTAL_CENTER_OFFSET,
				windowHeight - PhoneUtils.PHONE_BOTTOM_OFFSET - PhoneUtils.PHONE_HEIGHT, PhoneUtils.PHONE_WIDTH,
				PhoneUtils.PHONE_HEIGHT);

		// Buttons
		shutdownButton.render();
		homeButton.render();

	}
	
	private void drawBackground() {
		if (wallpaper == null) {
			PhoneUtils.drawOnPhone(PHONE_BACKGROUND, 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);
		} else {
			PhoneUtils.drawWallpaper(wallpaper, 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);
		}
	}
	
	public FontRenderer getFont() {
		return font;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		ListNBT list = new ListNBT();
		for (int i = 0; i < apps.size(); i++) {
			list.add(apps.get(i).serializeNBT());
		}
		compound.put("apps", list);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("apps", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			apps.get(i).deserializeNBT(list.getCompound(i));
		}
	}

	@Override
	public void recieveAddContactAck(UUID uuid, String name, boolean success) {
		for (ICommunicator communicator : communicators)
			communicator.recieveAddContactAck(uuid, name, success);
	}
	
	@Override
	public void recieveTextMessage(UUID source, String sourceName, String message) {
		for (ICommunicator communicator : communicators)
			communicator.recieveTextMessage(source, sourceName, message);
	}
	
	public void addCommunicator(ICommunicator communicator) {
		communicators.add(communicator);
	}
}
