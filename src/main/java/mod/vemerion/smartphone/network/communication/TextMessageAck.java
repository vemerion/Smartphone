package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class TextMessageAck {

	private UUID messageId;

	public TextMessageAck(UUID messageId) {
		this.messageId = messageId;
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeUUID(messageId);
	}

	public static TextMessageAck decode(final FriendlyByteBuf buffer) {
		return new TextMessageAck(buffer.readUUID());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final var context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			var sender = context.getSender();
			if (sender != null) {
				sender.getCapability(PhoneState.CAPABILITY).ifPresent(s -> s.removePendingTextMessage(messageId));
			}
		});
	}
}
