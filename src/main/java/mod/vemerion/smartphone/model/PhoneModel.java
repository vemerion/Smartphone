package mod.vemerion.smartphone.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import mod.vemerion.smartphone.Main;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class PhoneModel extends Model {
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Main.MODID,
			"textures/entity/smartphone.png");

	
    public ModelRenderer middle;
    public ModelRenderer top1;
    public ModelRenderer bottom1;
    public ModelRenderer top2;
    public ModelRenderer top3;
    public ModelRenderer top4;
    public ModelRenderer top5;
    public ModelRenderer bottom2;
    public ModelRenderer bottom3;
    public ModelRenderer bottom4;
    public ModelRenderer bottom5;

    public PhoneModel() {
    	super(RenderType::getEntityTranslucent);
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.top1 = new ModelRenderer(this, 0, 56);
        this.top1.setRotationPoint(0.0F, -28.0F, 0.0F);
        this.top1.addBox(-15.0F, 0.0F, -2.0F, 30.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bottom1 = new ModelRenderer(this, 0, 82);
        this.bottom1.setRotationPoint(0.0F, 26.0F, 0.0F);
        this.bottom1.addBox(-15.0F, 0.0F, -2.0F, 30.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.middle = new ModelRenderer(this, 0, 0);
        this.middle.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.middle.addBox(-16.0F, -26.0F, -2.0F, 32.0F, 52.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.top3 = new ModelRenderer(this, 0, 67);
        this.top3.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.top3.addBox(-13.0F, 0.0F, -2.0F, 26.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.top2 = new ModelRenderer(this, 0, 62);
        this.top2.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.top2.addBox(-14.0F, 0.0F, -2.0F, 28.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.top4 = new ModelRenderer(this, 0, 72);
        this.top4.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.top4.addBox(-12.0F, 0.0F, -2.0F, 24.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bottom2 = new ModelRenderer(this, 0, 88);
        this.bottom2.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.bottom2.addBox(-14.0F, 0.0F, -2.0F, 28.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bottom3 = new ModelRenderer(this, 0, 93);
        this.bottom3.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.bottom3.addBox(-13.0F, 0.0F, -2.0F, 26.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.top5 = new ModelRenderer(this, 0, 77);
        this.top5.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.top5.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bottom4 = new ModelRenderer(this, 0, 98);
        this.bottom4.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.bottom4.addBox(-12.0F, 0.0F, -2.0F, 24.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bottom5 = new ModelRenderer(this, 0, 103);
        this.bottom5.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.bottom5.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.middle.addChild(this.top1);
        this.middle.addChild(this.bottom1);
        this.top2.addChild(this.top3);
        this.top1.addChild(this.top2);
        this.top3.addChild(this.top4);
        this.bottom1.addChild(this.bottom2);
        this.bottom2.addChild(this.bottom3);
        this.top4.addChild(this.top5);
        this.bottom3.addChild(this.bottom4);
        this.bottom4.addChild(this.bottom5);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.middle).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
