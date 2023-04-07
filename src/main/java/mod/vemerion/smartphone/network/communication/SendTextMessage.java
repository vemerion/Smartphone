package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SendTextMessage {

	private UUID destination;
	private String message;

	public SendTextMessage(UUID destination, String message) {
		this.destination = destination;
		this.message = message;
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeUUID(destination);
		buffer.writeUtf(message);
	}

	public static SendTextMessage decode(final FriendlyByteBuf buffer) {
		return new SendTextMessage(buffer.readUUID(), buffer.readUtf(55));
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final var context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			var sender = context.getSender();
			if (sender != null) {
				var reciever = sender.getServer().getPlayerList().getPlayer(destination);
				if (reciever != null) {
					var senderId = sender.getUUID();
					var messageId = UUID.randomUUID();
					var senderProfile = reciever.getServer().getProfileCache().get(senderId);
					var sourceName = senderProfile.isPresent() ? senderProfile.get().getName() : "";
					Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> reciever),
							new RecieveTextMessage(sender.getUUID(), messageId, sourceName, message));
					reciever.getCapability(PhoneState.CAPABILITY).ifPresent(s -> {
						s.storeTextMessage(senderId, messageId, sourceName, message);
					});
				}
			}
		});
	}
}
