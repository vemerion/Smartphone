package mod.vemerion.smartphone;

import com.mojang.blaze3d.matrix.MatrixStack;

import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.renderer.PhoneRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventSubscriber {

	@SubscribeEvent
	public static void renderPhoneUse(RenderHandEvent event) {
		AbstractClientPlayerEntity player = Minecraft.getInstance().player;
		ItemStack itemStack = event.getItemStack();
		Item item = itemStack.getItem();
		float partialTicks = event.getPartialTicks();
		if (item.equals(Main.SMARTPHONE_ITEM) && Minecraft.getInstance().currentScreen instanceof Phone) {
			event.setCanceled(true);
			return;
		}
		
		if (item.equals(Main.SMARTPHONE_ITEM) && player.getActiveItemStack().equals(itemStack)) {
			event.setCanceled(true);
			HandSide side = event.getHand() == Hand.MAIN_HAND ? player.getPrimaryHand()
					: player.getPrimaryHand().opposite();
			float offset = side == HandSide.LEFT ? -1 : 1;
			PhoneRenderer renderer = new PhoneRenderer();
			int maxDuration = itemStack.getUseDuration();
			float duration = (float) maxDuration - ((float) player.getItemInUseCount() - partialTicks + 1.0f);
			float progress = MathHelper.clamp(duration / maxDuration, 0, 1);
			MatrixStack matrix = event.getMatrixStack();
			matrix.push();
			matrix.translate(offset * (2 - 2 * progress), -1 + progress * 0.95, -3 + progress * 0.85);
			matrix.rotate(new net.minecraft.util.math.vector.Quaternion(0, offset * (-90 + progress * 90), 0, true));
			renderer.func_239207_a_(itemStack, TransformType.NONE, matrix, event.getBuffers(), event.getLight(), OverlayTexture.NO_OVERLAY);
			matrix.pop();
		}
	}
}
