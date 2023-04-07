package mod.vemerion.smartphone.network;

import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class LoadPhoneStateMessage {

	private CompoundTag state;
	private ListTag pendingMessages;

	public LoadPhoneStateMessage(CompoundTag state, ListTag pendingMessages) {
		this.state = state;
		this.pendingMessages = pendingMessages;
	}

	public void encode(final FriendlyByteBuf buffer) {
		var tag = new CompoundTag();
		tag.put("messages", pendingMessages);
		buffer.writeNbt(state);
		buffer.writeNbt(tag);
	}

	public static LoadPhoneStateMessage decode(final FriendlyByteBuf buffer) {
		return new LoadPhoneStateMessage(buffer.readNbt(), buffer.readNbt().getList("messages", Tag.TAG_COMPOUND));
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final var context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(
				() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> LoadPhoneState.load(state, pendingMessages)));
	}

	private static class LoadPhoneState {
		private static DistExecutor.SafeRunnable load(CompoundTag state, ListTag pendingMessages) {
			return new DistExecutor.SafeRunnable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					var mc = Minecraft.getInstance();
					if (mc != null) {
						var phone = new Phone();
						mc.setScreen(phone);
						phone.deserializeNBT(state);
						var messages = PhoneState.TextMessage.deserializeTextMessages(pendingMessages);
						for (var m : messages) {
							m.handle(phone);
						}
					}
				}
			};
		}
	}
}
