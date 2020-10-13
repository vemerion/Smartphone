package mod.vemerion.smartphone.network;

import java.util.function.Supplier;

import mod.vemerion.smartphone.capability.PhoneState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SavePhoneStateMessage {
	
	private CompoundNBT state;
	
	public SavePhoneStateMessage(CompoundNBT state) {
		this.state = state;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeCompoundTag(state);
	}

	public static SavePhoneStateMessage decode(final PacketBuffer buffer) {
		return new SavePhoneStateMessage(buffer.readCompoundTag());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			PlayerEntity player = context.getSender();
			if (player != null) {
				player.getCapability(PhoneState.CAPABILITY).ifPresent(s -> s.deserializeNBT(state));
			}
		});
	}
}
