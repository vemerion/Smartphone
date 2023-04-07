package mod.vemerion.smartphone;

import mod.vemerion.smartphone.model.PhoneModel;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventSubscriber {

	@SubscribeEvent
	public static void onRegisterEntityRendererLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(PhoneModel.MODEL_LAYER, PhoneModel::createLayer);
	}

	@SubscribeEvent
	public static void onCreativeModeTab(CreativeModeTabEvent.BuildContents event) {
		if (event.getTab() == CreativeModeTabs.SEARCH)
			event.accept(ModInit.SMARTPHONE);
	}

}
