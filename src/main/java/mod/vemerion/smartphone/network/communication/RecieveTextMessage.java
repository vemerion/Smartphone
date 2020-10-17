package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import mod.vemerion.smartphone.phone.ICommunicator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class RecieveTextMessage {

	private UUID source;
	private String message;
	
	public RecieveTextMessage(UUID source, String message) {
		this.source = source;
		this.message = message;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeUniqueId(source);
		buffer.writeString(message);
	}

	public static RecieveTextMessage decode(final PacketBuffer buffer) {
		return new RecieveTextMessage(buffer.readUniqueId(), buffer.readString());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> ProcessTextMessage.process(source, message)));
	}

	private static class ProcessTextMessage {
		private static Runnable process(UUID source, String message) {
			return new Runnable() {
				@Override
				public void run() {
					Minecraft mc = Minecraft.getInstance();
					if (mc != null && mc.currentScreen != null && mc.currentScreen instanceof ICommunicator) {
						((ICommunicator) mc.currentScreen).recieveTextMessage(source, message);
					}
				}
			};
		}
	}
}
