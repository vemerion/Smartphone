package mod.vemerion.smartphone.phone.app;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class JukeboxAppMusic extends AbstractTickableSoundInstance {

	private Player player;

	public JukeboxAppMusic(Player player, SoundEvent music) {
		super(music, SoundSource.PLAYERS, player.getRandom());
		this.player = player;
		this.x = (float) player.getX();
		this.y = (float) player.getY();
		this.z = (float) player.getZ();
	}

	public void finish() {
		super.stop();
	}

	@Override
	public void tick() {
		x = (float) player.getX();
		y = (float) player.getY();
		z = (float) player.getZ();

	}
}
