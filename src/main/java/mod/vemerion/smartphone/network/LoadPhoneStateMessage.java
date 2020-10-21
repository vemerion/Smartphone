package mod.vemerion.smartphone.network;

import java.util.List;
import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class LoadPhoneStateMessage {

	private CompoundNBT state;
	private ListNBT pendingMessages;

	public LoadPhoneStateMessage(CompoundNBT state, ListNBT pendingMessages) {
		this.state = state;
		this.pendingMessages = pendingMessages;
	}

	public void encode(final PacketBuffer buffer) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("messages", pendingMessages);
		buffer.writeCompoundTag(state);
		buffer.writeCompoundTag(nbt);
	}

	public static LoadPhoneStateMessage decode(final PacketBuffer buffer) {
		return new LoadPhoneStateMessage(buffer.readCompoundTag(),
				buffer.readCompoundTag().getList("messages", Constants.NBT.TAG_COMPOUND));
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> LoadPhoneState.load(state, pendingMessages)));
	}

	private static class LoadPhoneState {
		private static DistExecutor.SafeRunnable load(CompoundNBT state, ListNBT pendingMessages) {
			return new DistExecutor.SafeRunnable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					Minecraft mc = Minecraft.getInstance();
					if (mc != null) {
						Phone phone = new Phone();
						Minecraft.getInstance().displayGuiScreen(phone);
						phone.deserializeNBT(state);
						List<PhoneState.TextMessage> messages = PhoneState.TextMessage
								.deserializeTextMessages(pendingMessages);
						for (PhoneState.TextMessage m : messages) {
							m.handle(phone);
						}
					}
				}
			};
		}
	}
}
