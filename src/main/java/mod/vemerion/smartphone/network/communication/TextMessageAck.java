package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class TextMessageAck {
	
	private UUID messageId;
	
	public TextMessageAck(UUID messageId) {
		this.messageId = messageId;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeUniqueId(messageId);
	}

	public static TextMessageAck decode(final PacketBuffer buffer) {
		return new TextMessageAck(buffer.readUniqueId());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();
			if (sender != null) {
				sender.getCapability(PhoneState.CAPABILITY).ifPresent(s -> s.removePendingTextMessage(messageId));
			}
		});
	}
}
