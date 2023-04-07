package mod.vemerion.smartphone.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.network.LoadPhoneStateMessage;
import mod.vemerion.smartphone.network.Network;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

public class PhoneState implements INBTSerializable<CompoundTag> {
	public static final Capability<PhoneState> CAPABILITY = CapabilityManager.get(new CapabilityToken<PhoneState>() {
	});

	private CompoundTag state;
	private List<TextMessage> pendingMessages;

	public PhoneState() {
		state = new CompoundTag();
		pendingMessages = new ArrayList<>();
	}

	@Override
	public CompoundTag serializeNBT() {
		var compound = new CompoundTag();
		compound.put("state", state);
		compound.put("messages", TextMessage.serializeTextMessages(pendingMessages));
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		state = nbt.getCompound("state");
		var msgs = nbt.getList("messages", Tag.TAG_COMPOUND);
		pendingMessages = TextMessage.deserializeTextMessages(msgs);
	}

	public void storeTextMessage(UUID uniqueID, UUID messageId, String senderName, String text) {
		pendingMessages.add(new TextMessage(uniqueID, messageId, senderName, text));
	}

	public void sendLoadStateMessage(ServerPlayer destination) {
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

	public void setState(CompoundTag s) {
		this.state = s;
	}

	@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
	public static class PhoneStorageProvider implements ICapabilitySerializable<CompoundTag> {

		private LazyOptional<PhoneState> instance = LazyOptional.of(PhoneState::new);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return CAPABILITY.orEmpty(cap, instance);
		}

		@Override
		public CompoundTag serializeNBT() {
			return instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!"))
					.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!"))
					.deserializeNBT(nbt);
		}

		public static final ResourceLocation LOCATION = new ResourceLocation(Main.MODID, "phonestorage");

		@SubscribeEvent
		public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player)
				event.addCapability(LOCATION, new PhoneStorageProvider());
		}
	}

	public static class TextMessage implements INBTSerializable<CompoundTag> {
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
		public CompoundTag serializeNBT() {
			var compound = new CompoundTag();
			compound.putUUID("sender", sender);
			compound.putUUID("messageId", messageId);
			compound.putString("senderName", senderName);
			compound.putString("text", text);
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			sender = nbt.getUUID("sender");
			messageId = nbt.getUUID("messageId");
			senderName = nbt.getString("senderName");
			text = nbt.getString("text");
		}

		public static ListTag serializeTextMessages(List<TextMessage> messages) {
			var msgs = new ListTag();
			for (TextMessage m : messages)
				msgs.add(m.serializeNBT());
			return msgs;
		}

		public static List<TextMessage> deserializeTextMessages(ListTag nbt) {
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
