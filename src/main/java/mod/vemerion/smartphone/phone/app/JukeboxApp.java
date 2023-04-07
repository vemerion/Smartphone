package mod.vemerion.smartphone.phone.app;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class JukeboxApp extends App {
	private static final SoundEvent[] MUSIC = { SoundEvents.MUSIC_DISC_11, SoundEvents.MUSIC_DISC_13,
			SoundEvents.MUSIC_DISC_BLOCKS, SoundEvents.MUSIC_DISC_CAT, SoundEvents.MUSIC_DISC_CHIRP,
			SoundEvents.MUSIC_DISC_FAR, SoundEvents.MUSIC_DISC_MALL, SoundEvents.MUSIC_DISC_MELLOHI,
			SoundEvents.MUSIC_DISC_STAL, SoundEvents.MUSIC_DISC_STRAD, SoundEvents.MUSIC_DISC_WAIT,
			SoundEvents.MUSIC_DISC_WARD };

	private static final ResourceLocation[] ICONS = {
			new ResourceLocation("minecraft", "textures/item/music_disc_11.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_13.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_blocks.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_cat.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_chirp.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_far.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_mall.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_mellohi.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_stal.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_strad.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_wait.png"),
			new ResourceLocation("minecraft", "textures/item/music_disc_ward.png") };

	private static final int MUSIC_COUNT = MUSIC.length;
	private static final float BUTTON_SIZE = PhoneUtils.APP_WIDTH / 3;

	private static final ResourceLocation JUKEBOX = new ResourceLocation("minecraft",
			"textures/item/music_disc_13.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/jukebox_app/jukebox_background.png");

	private static final ResourceLocation CANCEL_MUSIC = new ResourceLocation(Main.MODID,
			"textures/gui/jukebox_app/cancel_music.png");

	private List<Button> musicButtons;
	private Button cancelMusicButton;
	private JukeboxAppMusic activeMusic;

	public JukeboxApp(Phone phone) {
		super(phone);

		musicButtons = new ArrayList<>();
		for (int i = 0; i < MUSIC_COUNT; i++) {
			float x = (i % 3) * BUTTON_SIZE + 1;
			float y = (i / 3) * BUTTON_SIZE;
			var music = MUSIC[i];
			var icon = ICONS[i];
			musicButtons.add(new Button(new Rectangle(x, y, BUTTON_SIZE), () -> icon, phone, () -> {
				if (activeMusic != null)
					activeMusic.finish();
				var mc = Minecraft.getInstance();
				activeMusic = new JukeboxAppMusic(mc.player, music);
				Minecraft.getInstance().getSoundManager().play(activeMusic);
			}));
		}

		cancelMusicButton = new Button(
				new Rectangle((MUSIC_COUNT % 3) * BUTTON_SIZE + 1, (MUSIC_COUNT / 3) * BUTTON_SIZE, BUTTON_SIZE),
				() -> CANCEL_MUSIC, phone, () -> {
					if (activeMusic != null)
						activeMusic.finish();
				});
	}

	@Override
	public void tick() {
		super.tick();

		for (var b : musicButtons)
			b.tick();

		cancelMusicButton.tick();
	}

	@Override
	public void render(PoseStack matrix) {
		super.render(matrix);

		for (var b : musicButtons)
			b.render();

		cancelMusicButton.render();
	}

	@Override
	public void shutdown() {
		if (activeMusic != null)
			activeMusic.finish();
	}

	@Override
	public ResourceLocation getIcon() {
		return JUKEBOX;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

}
