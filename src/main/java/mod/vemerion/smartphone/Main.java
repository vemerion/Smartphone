package mod.vemerion.smartphone;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod(Main.MODID)
public class Main {
	public static final String MODID = "smartphone";
	
	@ObjectHolder(Main.MODID + ":smartphone_item")
	public static final Item SMARTPHONE_ITEM = null;
	
	@ObjectHolder("smartphone:catch_apple_sound")
	public static final SoundEvent CATCH_APPLE_SOUND = null;

	@ObjectHolder("smartphone:click_sound")
	public static final SoundEvent CLICK_SOUND = null;

	@ObjectHolder("smartphone:jump_sound")
	public static final SoundEvent JUMP_SOUND = null;
	
	@ObjectHolder("smartphone:write_sound")
	public static final SoundEvent WRITE_SOUND = null;                                                   
}
