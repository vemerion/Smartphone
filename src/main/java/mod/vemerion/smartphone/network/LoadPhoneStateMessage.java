package mod.vemerion.smartphone.network;

import java.util.function.Supplier;

import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class LoadPhoneStateMessage {

	private CompoundNBT state;

	public LoadPhoneStateMessage(CompoundNBT state) {
		this.state = state;
	}

	public void encode(final PacketBuffer buffer) {
		buffer.writeCompoundTag(state);
	}

	public static LoadPhoneStateMessage decode(final PacketBuffer buffer) {
		return new LoadPhoneStateMessage(buffer.readCompoundTag());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		final NetworkEvent.Context context = supplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> LoadPhoneState.load(state)));
	}

	private static class LoadPhoneState {
		private static Runnable load(CompoundNBT state) {
			return new Runnable() {
				@Override
				public void run() {
					Minecraft mc = Minecraft.getInstance();
					if (mc != null) {
						Phone phone = new Phone();
						Minecraft.getInstance().displayGuiScreen(phone);
						phone.deserializeNBT(state);
					}
				}
			};
		}
	}
}
