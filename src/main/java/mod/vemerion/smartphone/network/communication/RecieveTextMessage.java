package mod.vemerion.smartphone.network.communication;

import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import mod.vemerion.smartphone.ModInit;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.phone.ICommunicator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeUUID(source);
		buffer.writeUUID(messageId);
		buffer.writeUtf(sourceName);
		buffer.writeUtf(message);
	}

	public static RecieveTextMessage decode(final FriendlyByteBuf buffer) {
		return new RecieveTextMessage(buffer.readUUID(), buffer.readUUID(), buffer.readUtf(32767),
				buffer.readUtf(32767));
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final var context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT,
				() -> ProcessTextMessage.process(source, messageId, sourceName, message)));
	}

	private static class ProcessTextMessage {
		private static DistExecutor.SafeRunnable process(UUID source, UUID messageId, String sourceName,
				String message) {
			return new DistExecutor.SafeRunnable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					var mc = Minecraft.getInstance();
					var player = mc.player;
					if (mc != null && mc.screen != null && mc.screen instanceof ICommunicator) {
						((ICommunicator) mc.screen).recieveTextMessage(source, sourceName, message);
						player.playSound(ModInit.CATCH_APPLE.get(), 1, 1);
						Network.INSTANCE.send(PacketDistributor.SERVER.noArg(), new TextMessageAck(messageId));
					} else if (player.getInventory().hasAnyOf(ImmutableSet.of(ModInit.SMARTPHONE.get()))) {
						player.playSound(ModInit.CATCH_APPLE.get(), 1, 1);
					}
				}
			};
		}
	}
}
