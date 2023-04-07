package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class AddContactMessage {
	
	private String contactName;
	
	public AddContactMessage(String contact) {
		this.contactName = contact;
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeUtf(contactName);
	}

	public static AddContactMessage decode(final FriendlyByteBuf buffer) {
		return new AddContactMessage(buffer.readUtf(20));
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final var context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			var sender = context.getSender();
			if (sender != null) {
				var contact = sender.getServer().getPlayerList().getPlayerByName(contactName);
				var uuid = UUID.randomUUID();
				var name = "";
				boolean success = false;
				if (contact != null) {
					uuid = contact.getUUID();
					name = contact.getName().getString();
					success = true;
				}
				Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new AddContactAckMessage(uuid, name, success));
			}
		});
	}
}
