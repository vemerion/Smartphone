package mod.vemerion.smartphone;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModInit {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

	public static final RegistryObject<SmartphoneItem> SMARTPHONE = ITEMS.register("smartphone",
			SmartphoneItem::new);

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			Main.MODID);

	public static final RegistryObject<SoundEvent> CATCH_APPLE = SOUNDS.register("catch_apple",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "catch_apple")));
	public static final RegistryObject<SoundEvent> CLICK = SOUNDS.register("click",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "click")));
	public static final RegistryObject<SoundEvent> JUMP = SOUNDS.register("jump",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "jump")));
	public static final RegistryObject<SoundEvent> WRITE = SOUNDS.register("write",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, "write")));

}
