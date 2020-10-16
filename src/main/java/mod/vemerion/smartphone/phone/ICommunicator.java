package mod.vemerion.smartphone.phone;

import java.util.UUID;

import mod.vemerion.smartphone.network.AddContactMessage;
import mod.vemerion.smartphone.network.Network;
import net.minecraftforge.fml.network.PacketDistributor;

public interface ICommunicator {
	default void sendAddContactMessage(String name) {
		Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new AddContactMessage(name));
	}

	void recieveAddContactAck(UUID uuid, String name, boolean success);
}
