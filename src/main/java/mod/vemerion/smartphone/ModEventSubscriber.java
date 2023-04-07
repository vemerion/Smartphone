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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PhoneState.class);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		Network.INSTANCE.registerMessage(0, SavePhoneStateMessage.class, SavePhoneStateMessage::encode,
				SavePhoneStateMessage::decode, SavePhoneStateMessage::handle);
		Network.INSTANCE.registerMessage(1, LoadPhoneStateMessage.class, LoadPhoneStateMessage::encode,
				LoadPhoneStateMessage::decode, LoadPhoneStateMessage::handle);
		Network.INSTANCE.registerMessage(2, AddContactMessage.class, AddContactMessage::encode,
				AddContactMessage::decode, AddContactMessage::handle);
		Network.INSTANCE.registerMessage(3, AddContactAckMessage.class, AddContactAckMessage::encode,
				AddContactAckMessage::decode, AddContactAckMessage::handle);
		Network.INSTANCE.registerMessage(4, SendTextMessage.class, SendTextMessage::encode, SendTextMessage::decode,
				SendTextMessage::handle);
		Network.INSTANCE.registerMessage(5, RecieveTextMessage.class, RecieveTextMessage::encode,
				RecieveTextMessage::decode, RecieveTextMessage::handle);
		Network.INSTANCE.registerMessage(6, TextMessageAck.class, TextMessageAck::encode, TextMessageAck::decode,
				TextMessageAck::handle);
	}
}
