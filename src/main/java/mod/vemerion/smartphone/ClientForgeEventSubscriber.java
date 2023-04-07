package mod.vemerion.smartphone;

import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.TransformationHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventSubscriber {

	@SubscribeEvent
	public static void renderPhoneUse(RenderHandEvent event) {
		var mc = Minecraft.getInstance();
		var player = mc.player;
		var itemStack = event.getItemStack();
		var item = itemStack.getItem();
		float partialTicks = event.getPartialTick();
		if (item.equals(ModInit.SMARTPHONE.get()) && mc.screen instanceof Phone) {
			event.setCanceled(true);
			return;
		}

		if (item.equals(ModInit.SMARTPHONE.get()) && player.getUseItem().equals(itemStack)) {
			event.setCanceled(true);
			var side = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm()
					: player.getMainArm().getOpposite();
			float offset = side == HumanoidArm.LEFT ? -1 : 1;
			var renderer = IClientItemExtensions.of(item).getCustomRenderer();
			float progress = Mth.clamp((player.getTicksUsingItem() + partialTicks) / itemStack.getUseDuration(), 0, 1);
			var matrix = event.getPoseStack();
			matrix.pushPose();
			matrix.translate(offset * (2 - 2 * progress), -1 + progress * 0.95, -3 + progress * 0.85);
			matrix.mulPose(TransformationHelper.quatFromXYZ(0, offset * (-90 + progress * 90), 0, true));
			renderer.renderByItem(itemStack, ItemDisplayContext.NONE, matrix, event.getMultiBufferSource(),
					event.getPackedLight(), OverlayTexture.NO_OVERLAY);
			matrix.popPose();
		}
	}
}
