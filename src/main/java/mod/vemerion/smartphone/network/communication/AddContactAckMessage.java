package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.phone.ICommunicator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class AddContactAckMessage {

	private UUID uuid;
	private String name;
	private boolean success;
	
	public AddContactAckMessage(UUID uuid, String name, boolean success) {
		this.uuid = uuid;
		this.name = name;
		this.success = success;
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeUUID(uuid);
		buffer.writeUtf(name);
		buffer.writeBoolean(success);
	}

	public static AddContactAckMessage decode(final FriendlyByteBuf buffer) {
		return new AddContactAckMessage(buffer.readUUID(), buffer.readUtf(32767), buffer.readBoolean());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ProcessAddContactAck.process(uuid, name, success)));
	}

	private static class ProcessAddContactAck {
		private static DistExecutor.SafeRunnable process(UUID uuid, String name, boolean success) {
			return new DistExecutor.SafeRunnable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					Minecraft mc = Minecraft.getInstance();
					if (mc != null && mc.screen != null && mc.screen instanceof ICommunicator) {
						((ICommunicator) mc.screen).recieveAddContactAck(uuid, name, success);
					}
				}
			};
		}
	}
}
