package mod.vemerion.smartphone.network;

import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SavePhoneStateMessage {

	private CompoundTag state;

	public SavePhoneStateMessage(CompoundTag state) {
		this.state = state;
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeNbt(state);
	}

	public static SavePhoneStateMessage decode(final FriendlyByteBuf buffer) {
		return new SavePhoneStateMessage(buffer.readNbt());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			var player = context.getSender();
			if (player != null) {
				player.getCapability(PhoneState.CAPABILITY).ifPresent(s -> s.setState(state));
			}
		});
	}
}
