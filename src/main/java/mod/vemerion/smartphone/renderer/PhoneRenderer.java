package mod.vemerion.smartphone.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import mod.vemerion.smartphone.model.PhoneModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class PhoneRenderer extends ItemStackTileEntityRenderer {
	private final PhoneModel staff = new PhoneModel();

	@Override
	public void func_239207_a_(ItemStack itemStackIn, ItemCameraTransforms.TransformType transform, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		matrixStackIn.push();
		matrixStackIn.scale(1.0F, -1.0F, -1.0F);
		matrixStackIn.scale(0.5f, 0.5f, 0.5f);
		IVertexBuilder builder = ItemRenderer.getBuffer(bufferIn,
				this.staff.getRenderType(PhoneModel.TEXTURE_LOCATION), false, itemStackIn.hasEffect());
		this.staff.render(matrixStackIn, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1F);
		matrixStackIn.pop();
	}
}
