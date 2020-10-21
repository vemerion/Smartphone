package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.network.Network;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class AddContactMessage {
	
	private String contactName;
	
	public AddContactMessage(String contact) {
		this.contactName = contact;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeString(contactName);
	}

	public static AddContactMessage decode(final PacketBuffer buffer) {
		return new AddContactMessage(buffer.readString());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();
			if (sender != null) {
				PlayerEntity contact = sender.getServer().getPlayerList().getPlayerByUsername(contactName);
				UUID uuid = UUID.randomUUID();
				String name = "";
				boolean success = false;
				if (contact != null) {
					uuid = contact.getUniqueID();
					name = contact.getName().getString();
					success = true;
				}
				Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new AddContactAckMessage(uuid, name, success));
			}
		});
	}
}
