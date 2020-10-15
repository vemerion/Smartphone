package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Position;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class RunnerApp extends App {

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/runner_app/icon.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/runner_app/background.png");
	private static final ResourceLocation STEVE = new ResourceLocation(Main.MODID, "textures/gui/runner_app/steve.png");
	private static final ResourceLocation FENCE = new ResourceLocation(Main.MODID, "textures/gui/runner_app/fence.png");
	private static final ResourceLocation FADE = new ResourceLocation(Main.MODID, "textures/gui/runner_app/fade.png");

	private static final float PLAYER_SIZE = 40;

	Position player;
	private float jump;
	private float z;
	private List<Position> fences;
	private int fenceCooldown;
	private boolean gameOver;

	public RunnerApp(Phone phone) {
		super(phone);
		resume();
	}

	@Override
	public void tick() {
		super.tick();

		if (gameOver)
			return;
		
		if (ticksExisted % 3 == 0)
			phone.playSound(SoundEvents.BLOCK_GRASS_STEP, 1);
		
		for (Position fence : fences) {
			fence.y += 4;
			if (z < 0.05f
					&& new Rectangle(fence.x, fence.y + 13, 100, 2).intersect(new Rectangle(player.x - PLAYER_SIZE / 2f,
							player.y - PLAYER_SIZE / 2f, PLAYER_SIZE, PLAYER_SIZE / 2))) {
				player.y += 4;
			}
		}

		if (phone.isKeyDown(GLFW.GLFW_KEY_SPACE) && z < 0.01f) {
			jump = 1f;
			phone.playSound(Main.JUMP_SOUND, 0.6f);
		}

		if (z < 0.1f) {
			if (phone.isKeyDown(GLFW.GLFW_KEY_LEFT))
				player.x -= 3;
			if (phone.isKeyDown(GLFW.GLFW_KEY_RIGHT))
				player.x += 3;
		}

		player.x = MathHelper.clamp(player.x, PLAYER_SIZE / 2, 100 - PLAYER_SIZE / 2);

		z += jump - 0.7f;
		jump *= 0.97;
		if (z < 0)
			z = 0;

		if (fenceCooldown-- < 0 && rand.nextDouble() < 0.03) {
			fences.add(new Position(0, -5));
			fenceCooldown = 20;
		}

		for (int i = fences.size() - 1; i >= 0; i--) {
			if (fences.get(i).y > 200)
				fences.remove(i);
		}
		
		if (player.y > 200)
			gameOver = true;
	}

	@Override
	public void resume() {
		player = new Position(50, 80);
		fences = new ArrayList<>();
		gameOver = false;
	}

	private float playerSize() {
		return PLAYER_SIZE + z * 10;
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
	public void render() {
		PhoneUtils.drawOnPhone(getBackground(), 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT, 0,
				1 - ticksExisted / 100f % 100f, 1, 0.5f);

		for (Position fence : fences) {
			PhoneUtils.drawOnPhone(FENCE, fence.x, fence.y, 100, 15);
		}

		float playerSize = playerSize();
		PhoneUtils.drawOnPhone(STEVE, player.x - playerSize / 2, player.y - playerSize / 2, playerSize, playerSize,
				ticksExisted % 20 / 5 / 4f + 1 / 4f, 0, -1 / 4f, 1);
		
		if (gameOver) {
			PhoneUtils.drawOnPhone(FADE, 0, 0, 100, 200);
			PhoneUtils.writeOnPhone(font, "Game", 18, 85, new Color(255, 50, 50), 2);
			PhoneUtils.writeOnPhone(font, "Over", 18, 115, new Color(255, 50, 50), 2);
		}

	}
}
