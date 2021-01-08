package mod.vemerion.smartphone;

import mod.vemerion.smartphone.capability.PhoneState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class ForgeEventSubscriber {

	@SubscribeEvent
	public static void clonePhoneState(PlayerEvent.Clone event) {
		event.getPlayer().getCapability(PhoneState.CAPABILITY).ifPresent(newState -> {
			event.getOriginal().getCapability(PhoneState.CAPABILITY).ifPresent(oldState -> {
				newState.deserializeNBT(oldState.serializeNBT());
			});
		});
	}
}
