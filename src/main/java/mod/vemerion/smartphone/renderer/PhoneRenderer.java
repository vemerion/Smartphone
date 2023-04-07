package mod.vemerion.smartphone.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.model.PhoneModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PhoneRenderer extends BlockEntityWithoutLevelRenderer {

	private final PhoneModel model;

	public PhoneRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
		super(pBlockEntityRenderDispatcher, pEntityModelSet);
		model = new PhoneModel(pEntityModelSet.bakeLayer(PhoneModel.MODEL_LAYER));
	}

	@Override
	public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transform, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.pushPose();
		matrixStackIn.scale(1.0F, -1.0F, -1.0F);
		matrixStackIn.scale(0.5f, 0.5f, 0.5f);
		var builder = ItemRenderer.getFoilBuffer(bufferIn, model.renderType(PhoneModel.TEXTURE_LOCATION), false,
				itemStackIn.hasFoil());
		model.renderToBuffer(matrixStackIn, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1F);
		matrixStackIn.popPose();
	}
}
