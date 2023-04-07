package mod.vemerion.smartphone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main {
	public static final String MODID = "smartphone";

	public Main() {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		ModInit.ITEMS.register(bus);
		ModInit.SOUNDS.register(bus);
	}
}
