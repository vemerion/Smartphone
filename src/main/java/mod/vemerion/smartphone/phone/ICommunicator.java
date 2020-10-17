package mod.vemerion.smartphone.phone;

import java.util.UUID;

import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.network.communication.AddContactMessage;
import mod.vemerion.smartphone.network.communication.SendTextMessage;
import net.minecraftforge.fml.network.PacketDistributor;

public interface ICommunicator {
	default void sendAddContactMessage(String name) {
		Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new AddContactMessage(name));
	}

	void recieveAddContactAck(UUID uuid, String name, boolean success);
	
	default void sendTextMessage(UUID destination, String message) {
		Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new SendTextMessage(destination, message));
	}

	void recieveTextMessage(UUID source, String message);
}
