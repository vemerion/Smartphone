package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.network.Network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class SendTextMessage {

	private UUID destination;
	private String message;

	public SendTextMessage(UUID destination, String message) {
		this.destination = destination;
		this.message = message;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeUniqueId(destination);
		buffer.writeString(message);
	}

	public static SendTextMessage decode(final PacketBuffer buffer) {
		return new SendTextMessage(buffer.readUniqueId(), buffer.readString());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();
			if (sender != null) {
				ServerPlayerEntity reciever = sender.getServer().getPlayerList().getPlayerByUUID(destination);
				if (reciever != null) {
					Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> reciever),
							new RecieveTextMessage(sender.getUniqueID(), message));
				}
			}
		});
	}
}
