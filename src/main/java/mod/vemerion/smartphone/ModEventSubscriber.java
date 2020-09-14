package mod.vemerion.smartphone;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
	
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(setup(new SmartphoneItem(), "smartphone_item"));
	}
	
	@SubscribeEvent
	public static void onRegisterSound(RegistryEvent.Register<SoundEvent> event) {
		SoundEvent catch_apple_sound = new SoundEvent(new ResourceLocation(Main.MODID, "catch_apple_sound"));
		event.getRegistry().register(setup(catch_apple_sound, "catch_apple_sound"));
		SoundEvent click_sound = new SoundEvent(new ResourceLocation(Main.MODID, "click_sound"));
		event.getRegistry().register(setup(click_sound, "click_sound"));
		SoundEvent jump_sound = new SoundEvent(new ResourceLocation(Main.MODID, "jump_sound"));
		event.getRegistry().register(setup(jump_sound, "jump_sound"));
		SoundEvent write_sound = new SoundEvent(new ResourceLocation(Main.MODID, "write_sound"));
		event.getRegistry().register(setup(write_sound, "write_sound"));
	}

	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
		return setup(entry, new ResourceLocation(Main.MODID, name));
	}

	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}

}
