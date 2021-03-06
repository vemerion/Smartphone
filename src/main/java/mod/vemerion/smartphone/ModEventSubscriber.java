package mod.vemerion.smartphone;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.network.LoadPhoneStateMessage;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.network.SavePhoneStateMessage;
import mod.vemerion.smartphone.network.communication.AddContactAckMessage;
import mod.vemerion.smartphone.network.communication.AddContactMessage;
import mod.vemerion.smartphone.network.communication.RecieveTextMessage;
import mod.vemerion.smartphone.network.communication.SendTextMessage;
import mod.vemerion.smartphone.network.communication.TextMessageAck;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
	
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(setup(new SmartphoneItem(), "smartphone_item"));
	}
	
	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(PhoneState.class, new PhoneState.PhoneStateStorage(), PhoneState::new);
		
		Network.INSTANCE.registerMessage(0, SavePhoneStateMessage.class, SavePhoneStateMessage::encode,
				SavePhoneStateMessage::decode, SavePhoneStateMessage::handle);
		Network.INSTANCE.registerMessage(1, LoadPhoneStateMessage.class, LoadPhoneStateMessage::encode,
				LoadPhoneStateMessage::decode, LoadPhoneStateMessage::handle);
		Network.INSTANCE.registerMessage(2, AddContactMessage.class, AddContactMessage::encode,
				AddContactMessage::decode, AddContactMessage::handle);
		Network.INSTANCE.registerMessage(3, AddContactAckMessage.class, AddContactAckMessage::encode,
				AddContactAckMessage::decode, AddContactAckMessage::handle);
		Network.INSTANCE.registerMessage(4, SendTextMessage.class, SendTextMessage::encode,
				SendTextMessage::decode, SendTextMessage::handle);
		Network.INSTANCE.registerMessage(5, RecieveTextMessage.class, RecieveTextMessage::encode,
				RecieveTextMessage::decode, RecieveTextMessage::handle);
		Network.INSTANCE.registerMessage(6,	TextMessageAck.class, TextMessageAck::encode,
				TextMessageAck::decode, TextMessageAck::handle);
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
