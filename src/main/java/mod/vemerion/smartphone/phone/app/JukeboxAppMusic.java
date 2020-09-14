package mod.vemerion.smartphone.phone.app;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class JukeboxAppMusic extends TickableSound {
	
	private PlayerEntity player;

	public JukeboxAppMusic(PlayerEntity player, SoundEvent music) {
		super(music, SoundCategory.PLAYERS);
		this.player = player;
		this.x = (float) player.getPosX();
		this.y = (float) player.getPosY();
		this.z = (float) player.getPosZ();
	}
	
	public void stop() {
		donePlaying = true;
	}

	@Override
	public void tick() {
		x = (float) player.getPosX();
		y = (float) player.getPosY();
		z = (float) player.getPosZ();

	}
}
