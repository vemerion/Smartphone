package mod.vemerion.smartphone.capability;

import mod.vemerion.smartphone.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;


public class PhoneState implements INBTSerializable<CompoundNBT> {
	@CapabilityInject(PhoneState.class)
	public static final Capability<PhoneState> CAPABILITY = null;

	private CompoundNBT state;
	
	public PhoneState() {
		state = new CompoundNBT();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		return state;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		state = nbt;
	}
	
	@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
	public static class PhoneStorageProvider implements ICapabilitySerializable<INBT> {

		private LazyOptional<PhoneState> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return CAPABILITY.orEmpty(cap, instance);
		}

		@Override
		public INBT serializeNBT() {
			return CAPABILITY.getStorage().writeNBT(CAPABILITY,
					instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
		}

		@Override
		public void deserializeNBT(INBT nbt) {
			CAPABILITY.getStorage().readNBT(CAPABILITY,
					instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null,
					nbt);
		}

		public static final ResourceLocation LOCATION = new ResourceLocation(Main.MODID,
				"phonestorage");

		@SubscribeEvent
		public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof PlayerEntity)
				event.addCapability(LOCATION, new PhoneStorageProvider());
		}
	}

	public static class PhoneStateStorage implements IStorage<PhoneState> {

		@Override
		public INBT writeNBT(Capability<PhoneState> capability, PhoneState instance, Direction side) {
			return instance.serializeNBT();

		}

		@Override
		public void readNBT(Capability<PhoneState> capability, PhoneState instance, Direction side,
				INBT nbt) {
			instance.deserializeNBT((CompoundNBT) nbt);
		}
	}
}
