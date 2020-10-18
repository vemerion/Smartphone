package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.phone.ICommunicator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class RecieveTextMessage {

	private UUID source;
	private UUID messageId;
	private String sourceName;
	private String message;

	public RecieveTextMessage(UUID source, UUID messageId, String sourceName, String message) {
		this.source = source;
		this.messageId = messageId;
		this.sourceName = sourceName;
		this.message = message;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeUniqueId(source);
		buffer.writeUniqueId(messageId);
		buffer.writeString(sourceName);
		buffer.writeString(message);
	}

	public static RecieveTextMessage decode(final PacketBuffer buffer) {
		return new RecieveTextMessage(buffer.readUniqueId(), buffer.readUniqueId(), buffer.readString(), buffer.readString());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT,
				() -> ProcessTextMessage.process(source, messageId, sourceName, message)));
	}

	private static class ProcessTextMessage {
		private static Runnable process(UUID source, UUID messageId, String sourceName, String message) {
			return new Runnable() {
				@Override
				public void run() {
					Minecraft mc = Minecraft.getInstance();
					PlayerEntity player = mc.player;
					if (mc != null && mc.currentScreen != null && mc.currentScreen instanceof ICommunicator) {
						((ICommunicator) mc.currentScreen).recieveTextMessage(source, sourceName, message);
						player.playSound(Main.CATCH_APPLE_SOUND, 1, 1);
						Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new TextMessageAck(messageId));
					} else if (player.inventory.hasAny(ImmutableSet.of(Main.SMARTPHONE_ITEM))) {
						player.playSound(Main.CATCH_APPLE_SOUND, 1, 1);
					}
				}
			};
		}
	}
}
