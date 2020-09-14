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

public class CatchAppleApp extends App {
	private static final ResourceLocation APPLE = new ResourceLocation("minecraft", "textures/item/apple.png");
	private static final ResourceLocation BACKGROUND1 = new ResourceLocation(Main.MODID,
			"textures/gui/catch_apple_app/background1.png");
	private static final ResourceLocation BACKGROUND2 = new ResourceLocation(Main.MODID,
			"textures/gui/catch_apple_app/background2.png");
	private static final ResourceLocation STEVE = new ResourceLocation(Main.MODID,
			"textures/gui/catch_apple_app/steve.png");

	private static final float PLAYER_SIZE = 40;
	private static final float APPLE_SIZE = 20;

	private List<Position> apples;
	private Position player;
	private int ticksRunning;
	private boolean facingLeft;
	private int score;

	public CatchAppleApp(Phone phone) {
		super(phone);
		this.apples = new ArrayList<>();
		this.player = new Position(50, 200 - PLAYER_SIZE - 15);
	}

	@Override
	public void tick() {
		super.tick();
		if (rand.nextDouble() < 0.02) {
			apples.add(new Position(rand.nextFloat() * (100 - APPLE_SIZE), 0));
		}

		for (int i = apples.size() - 1; i >= 0; i--) {
			Position apple = apples.get(i);
			apple.y += 3;

			if (new Rectangle(player.x, player.y, PLAYER_SIZE).intersect(new Rectangle(apple.x, apple.y, APPLE_SIZE))) {
				apples.remove(i);
				score++;
				phone.playSound(Main.CATCH_APPLE_SOUND, 0.45f);
			} else if (apple.y > 175) {
				apples.remove(i);
			}

		}

		movePlayer(phone);
	}

	private void movePlayer(Phone phone) {
		if (phone.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			facingLeft = true;
			ticksRunning++;
			player.x -= 3;
			if (ticksRunning % 3 == 0)
				phone.playSound(SoundEvents.BLOCK_GRASS_STEP, 1);
		} else if (phone.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			facingLeft = false;
			ticksRunning++;
			player.x += 3;
			if (ticksRunning % 3 == 0)
				phone.playSound(SoundEvents.BLOCK_GRASS_STEP, 1);
		} else {
			ticksRunning = 0;
		}
		player.x = MathHelper.clamp(player.x, 0, 100 - PLAYER_SIZE);
	}

	@Override
	public void render() {
		super.render();

		for (Position apple : apples) {
			PhoneUtils.drawOnPhone(APPLE, apple.x, apple.y, APPLE_SIZE, APPLE_SIZE);
		}

		// Player
		if (facingLeft) {
			PhoneUtils.drawOnPhone(STEVE, player.x, player.y, PLAYER_SIZE, PLAYER_SIZE,
					ticksRunning % 20 / 5 / 4f + 1 / 4f, 0, -1 / 4f, 1);
		} else {
			PhoneUtils.drawOnPhone(STEVE, player.x, player.y, PLAYER_SIZE, PLAYER_SIZE, ticksRunning % 20 / 5 / 4f, 0,
					1 / 4f, 1);
		}

		// Score
		PhoneUtils.writeOnPhone("Score: " + score, 3, 190, new Color(255, 240, 0), 0.65f);

	}

	@Override
	public ResourceLocation getIcon() {
		return APPLE;
	}

	@Override
	public ResourceLocation getBackground() {
		if (ticksExisted % 20 < 10)
			return BACKGROUND1;
		else
			return BACKGROUND2;
	}

}
