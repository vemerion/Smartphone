package mod.vemerion.smartphone.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.network.LoadPhoneStateMessage;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

public class PhoneState implements INBTSerializable<CompoundNBT> {
	@CapabilityInject(PhoneState.class)
	public static final Capability<PhoneState> CAPABILITY = null;

	private CompoundNBT state;
	private List<TextMessage> pendingMessages;

	public PhoneState() {
		state = new CompoundNBT();
		pendingMessages = new ArrayList<>();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.put("state", state);
		compound.put("messages", TextMessage.serializeTextMessages(pendingMessages));
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		state = nbt.getCompound("state");
		ListNBT msgs = nbt.getList("messages", Constants.NBT.TAG_COMPOUND);
		pendingMessages = TextMessage.deserializeTextMessages(msgs);
	}

	public void storeTextMessage(UUID uniqueID, UUID messageId, String senderName, String text) {
		pendingMessages.add(new TextMessage(uniqueID, messageId, senderName, text));
	}
	
	public void sendLoadStateMessage(ServerPlayerEntity destination) {
		Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> destination),
				new LoadPhoneStateMessage(state, TextMessage.serializeTextMessages(pendingMessages)));
		pendingMessages = new ArrayList<>();
	}
	
	public void removePendingTextMessage(UUID messageId) {
		for (int i = pendingMessages.size() - 1; i >= 0; i--) {
			if (pendingMessages.get(i).messageId.equals(messageId))
				pendingMessages.remove(i);
		}
	}
	
	public void setState(CompoundNBT s) {
		this.state = s;
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

		public static final ResourceLocation LOCATION = new ResourceLocation(Main.MODID, "phonestorage");

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
		public void readNBT(Capability<PhoneState> capability, PhoneState instance, Direction side, INBT nbt) {
			instance.deserializeNBT((CompoundNBT) nbt);
		}
	}

	public static class TextMessage implements INBTSerializable<CompoundNBT> {
		private UUID sender;
		private UUID messageId;
		private String senderName;
		private String text;

		private TextMessage() {

		}

		private TextMessage(UUID sender, UUID messageId, String senderName, String text) {
			this.sender = sender;
			this.messageId = messageId;
			this.senderName = senderName;
			this.text = text;
		}
		
		public void handle(Phone phone) {
			phone.recieveTextMessage(sender, senderName, text);
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT compound = new CompoundNBT();
			compound.putUniqueId("sender", sender);
			compound.putUniqueId("messageId", messageId);
			compound.putString("senderName", senderName);
			compound.putString("text", text);
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			sender = nbt.getUniqueId("sender");
			messageId = nbt.getUniqueId("messageId");
			senderName = nbt.getString("senderName");
			text = nbt.getString("text");
		}
		
		public static ListNBT serializeTextMessages(List<TextMessage> messages) {
			ListNBT msgs = new ListNBT();
			for (TextMessage m : messages)
				msgs.add(m.serializeNBT());
			return msgs;
		}
		
		public static List<TextMessage> deserializeTextMessages(ListNBT nbt) {
			List<TextMessage> messages = new ArrayList<>();
			for (int i = 0; i < nbt.size(); i++) {
				TextMessage message = new TextMessage();
				message.deserializeNBT(nbt.getCompound(i));
				messages.add(message);
			}
			return messages;
		}
	}
}
