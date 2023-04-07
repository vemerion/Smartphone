package mod.vemerion.smartphone.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.vemerion.smartphone.Main;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PhoneModel extends Model {
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Main.MODID,
			"textures/entity/smartphone.png");

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(
			new ResourceLocation(Main.MODID, "phone_model"), "main");

	public ModelPart middle;
	public ModelPart top1;
	public ModelPart bottom1;
	public ModelPart top2;
	public ModelPart top3;
	public ModelPart top4;
	public ModelPart top5;
	public ModelPart bottom2;
	public ModelPart bottom3;
	public ModelPart bottom4;
	public ModelPart bottom5;

	public PhoneModel(ModelPart parts) {
		super(RenderType::entityTranslucent);
		this.middle = parts.getChild("middle");
		this.top1 = middle.getChild("top1");
		this.bottom1 = middle.getChild("bottom1");
		this.top2 = top1.getChild("top2");
		this.top3 = top2.getChild("top3");
		this.top4 = top3.getChild("top4");
		this.top5 = top4.getChild("top5");
		this.bottom2 = bottom1.getChild("bottom2");
		this.bottom3 = bottom2.getChild("bottom3");
		this.bottom4 = bottom3.getChild("bottom4");
		this.bottom5 = bottom4.getChild("bottom5");
	}

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay,
			float pRed, float pGreen, float pBlue, float pAlpha) {
		middle.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition parts = mesh.getRoot();
		PartDefinition middle = parts.addOrReplaceChild("middle",
				CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -26.0F, -2.0F, 32.0F, 52.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0, 0, 0));
		PartDefinition top1 = middle.addOrReplaceChild("top1",
				CubeListBuilder.create().texOffs(0, 56).addBox(-15.0F, 0.0F, -2.0F, 30.0F, 2.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, 0, 0, 0));
		PartDefinition bottom1 = middle.addOrReplaceChild("bottom1",
				CubeListBuilder.create().texOffs(0, 82).addBox(-15.0F, 0.0F, -2.0F, 30.0F, 2.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0, 0, 0));
		PartDefinition top2 = top1.addOrReplaceChild("top2",
				CubeListBuilder.create().texOffs(0, 62).addBox(-14.0F, 0.0F, -2.0F, 28.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0, 0, 0));
		PartDefinition top3 = top2.addOrReplaceChild("top3",
				CubeListBuilder.create().texOffs(0, 67).addBox(-13.0F, 0.0F, -2.0F, 26.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0, 0, 0));
		PartDefinition top4 = top3.addOrReplaceChild("top4",
				CubeListBuilder.create().texOffs(0, 72).addBox(-12.0F, 0.0F, -2.0F, 24.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0, 0, 0));
		top4.addOrReplaceChild("top5",
				CubeListBuilder.create().texOffs(0, 77).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0, 0, 0));
		PartDefinition bottom2 = bottom1.addOrReplaceChild("bottom2",
				CubeListBuilder.create().texOffs(0, 88).addBox(-14.0F, 0.0F, -2.0F, 28.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0, 0, 0));
		PartDefinition bottom3 = bottom2.addOrReplaceChild("bottom3",
				CubeListBuilder.create().texOffs(0, 93).addBox(-13.0F, 0.0F, -2.0F, 26.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0, 0, 0));
		PartDefinition bottom4 = bottom3.addOrReplaceChild("bottom4",
				CubeListBuilder.create().texOffs(0, 98).addBox(-12.0F, 0.0F, -2.0F, 24.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0, 0, 0));
		bottom4.addOrReplaceChild("bottom5",
				CubeListBuilder.create().texOffs(0, 103).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 1.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0, 0, 0));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
